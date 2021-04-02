(ns kuti.record
  (:refer-clojure :exclude [get list])
  (:require [clojure.java.io :as io]
            [crux.api :as crux]
            [crux.rocksdb :as rocks]
            [kosa.config :as config]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]
            [kuti.support :refer [path-join]]
            [kuti.support.digest :refer [uuid ->uuid]]
            [mount.core :refer [defstate]]
            [kuti.support.time :as time]
            [clojure.string :as clojure.string])
  (:import [java.math BigDecimal BigInteger]
           [java.lang String Boolean]))

(def crux-node)

(defn- data-dir []
  (get-in config/config [:db-spec :data-dir]))

(defn- crux-http-port []
  (get-in config/config [:db-spec :crux-http-port]))

(defn start-crux! []
  (letfn [(kv-store [dir]
            {:kv-store {:crux/module 'crux.rocksdb/->kv-store
	                      :db-dir      (io/file (data-dir) dir)
                        :sync?       true}})]
    (crux/start-node
     {:crux/tx-log              (kv-store "tx-log")
	    :crux/document-store      (kv-store "doc-store")
      :crux/index-store         (kv-store "index-store")
      :crux.lucene/lucene-store {:db-dir (path-join (data-dir) "lucene-dir")}
      :crux.http-server/server  {:port (crux-http-port)}})))

(defn stop-crux! []
  (.close crux-node))

(defstate crux-node
  :start (start-crux!)
  :stop  (stop-crux!))

(defn get [id]
  (crux/entity (crux/db crux-node) (->uuid id)))

(defn put-async* [datum]
  (crux/submit-tx crux-node [[:crux.tx/put datum]]))

(defn put-prepare [raw]
  (let [old-id (:crux.db/id raw)]
    (-> raw
        (assoc :crux.db/id (if old-id
                             (->uuid old-id)
                             (uuid)))
        (assoc :updated-at (time/now)))))

(defn validate-put! [e allowed-keys]
  (let [allowed-keys* (conj allowed-keys :crux.db/id :type :updated-at :published-at)
        extras (apply dissoc e allowed-keys*)]
    (when (not-empty extras)
      (throw (ex-info (format "Extra fields '%s' found during put."
                              (clojure.string/join ", " (keys extras)))
                      e)))))

(defn put-async
  "Try not to use me unless you absolutely have to. Prefer `put` (synchronous)."
  [e restricted-keys]
  (validate-put! e restricted-keys)
  (let [raw (select-keys e (conj restricted-keys :crux.db/id :type))
        doc (put-prepare raw)
        tx  (put-async* doc)]
    (assoc tx :crux.db/id (:crux.db/id doc))))

(defn put [e restricted-keys]
  (let [tx   (put-async e restricted-keys)
        _    (crux/await-tx crux-node tx)
        card (get (:crux.db/id tx))]
    card))

(defn delete-async
  "Try not to use me unless you absolutely have to. Prefer `delete` (syncronous)."
  [e]
  (let [id (:crux.db/id e)]
    (crux/submit-tx crux-node [[:crux.tx/delete id]])))

(defn delete [e]
  (let [tx   (delete-async e)
        _    (crux/await-tx crux-node tx)
        deleted e]
    deleted))

(defn query
  ([q]
   (let [result-set (crux/q (crux/db crux-node) q)]
     (map #(-> % first get) result-set)))
  ([q param]
   (let [result-set (crux/q (crux/db crux-node) q param)
         _ (prn result-set)]
     (map #(-> % first get) result-set))))

(defn list
  [type]
  (let [type-kw (name type)
        list-query '{:find     [e updated-at]
                     :where    [[e :type type]
                                [e :updated-at updated-at]]
                     :order-by [[updated-at :desc]]
                     :in [type]}]
    (query list-query type-kw)))

(defn non-homogenous? [e]
  (if-let [type (:type e)]
    (remove #(= type (-> % namespace keyword))
            (keys (dissoc e :crux.db/id :type)))
    (throw (IllegalArgumentException. ":type key not found."))))

(defn missing-key? [m k]
  (when (not (contains? m k))
    k))

(defn missing-keys? [m ks]
  (->> ks
       (map #(missing-key? m %))
       (remove nil?)))

(defn assert-required-attrs [e]
  (let [type (:type e)
        attrs (-> (query '{:find [e attrs]
                           :where [[e :db.entity/type t]
                                   [e :db.entity/attrs attrs]]
                           :in [t]} type)
                  first
                  :db.entity/attrs)
        missing (missing-keys? e attrs)]
    (assert (empty? missing)
            (format "Saved failed. Missing key(s) for entity of type '%s': %s"
                    type (clojure.string/join ", " missing)))))

(defn coerce [[k v]]
  (let [value (if (= (class v) clojure.lang.BigInt)
                (biginteger v)
                v)]
    [k value]))

(defn schema-for [k]
  (-> (query '{:find [e vt]
               :where [[e :db/ident ident]
                       [e :db/valueType vt]]
               :in [ident]} k)
      first))

(def value-types
  {:db.type/bigdec  java.math.BigDecimal
   :db.type/bigint  java.math.BigInteger
   :db.type/string  java.lang.String
   :db.type/boolean java.lang.Boolean
   ;; TODO: double
   ;; TODO: float
   :db.type/instant java.util.Date
   ;; TODO: `inst`? or another name for java.time.Instant?
   ;; TODO: keyword
   ;; TODO: long
   ;; TODO: string
   ;; TODO: symbol
   ;; TODO: tuple
   ;; TODO: uuid
   ;; TODO: URI
   ;; TODO: bytes
   })

(defn class-for [vt]
  (assert (contains? value-types vt)
          (format "No :db/valueType of '%s' exists." vt))
  (clojure.core/get value-types vt))

(defn assert-schema [e s]
  (let [k (:db/ident s)
        f (clojure.core/get e k)
        c (class f)
        v (:db/valueType s)
        t (class-for v)]
    (assert (= c t)
            (format "Class %s of field %s does not match value type %s" c k t))
    k))

(defn save! [e]
  (assert (contains? e :type) ":type key expected.")
  (assert (empty? (non-homogenous? e))
          (format "Some keys did not match specified :type. %s"
                  (clojure.string/join ", " (non-homogenous? e))))
  (assert-required-attrs e)
  (let [e2 (into {} (map coerce e))]
    (->> (disj (-> e2 keys set) :type :crux.db/id :updated-at)
         (map schema-for)
         (map #(assert-schema e2 %))
         (put e2))))
