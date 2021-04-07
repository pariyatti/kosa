(ns kuti.record.core
  (:refer-clojure :exclude [get list])
  (:require [clojure.java.io :as io]
            [crux.api :as crux]
            [crux.rocksdb :as rocks]
            [kosa.config :as config]
            [kuti.support :as support]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]
            [kuti.support :refer [path-join]]
            [kuti.support.digest :refer [uuid ->uuid]]
            [mount.core :refer [defstate]]
            [kuti.support.time :as time]
            [clojure.string :as clojure.string]))

(def meta-keys #{:crux.db/id :type :updated-at :published-at})
(def timestamp-fields #{:created-at :updated-at :published-at})

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

(defn transact! [node txns & [error-msg]]
  (let [tx (->> txns
                (crux/submit-tx node)
                (crux/await-tx node))]
    (when-not (crux/tx-committed? node tx)
      (throw (Exception. error-msg)))))

(defn get* [id]
  (crux/entity (crux/db crux-node) id))

(defn get [id]
  (get* (->uuid id)))

(defn timestamp [e]
  (let [ts (support/typify e :updated-at)
        _ (dbg "ts?")
        _ (dbg ts)]
    (assoc e ts (time/now))))

(defn put-async* [datum]
  (crux/submit-tx crux-node [[:crux.tx/put datum]]))

(defn put-prepare [raw]
  (let [old-id (:crux.db/id raw)]
    (-> raw
        (assoc :crux.db/id (if old-id
                             (->uuid old-id)
                             (uuid)))
        ;; (assoc :updated-at (time/now))
        )))

(defn validate-put! [e allowed-keys]
  (let [extras
        (->> meta-keys
             (apply conj allowed-keys)
             (apply dissoc e)
             (keys)
             (remove #(timestamp-fields (-> % name keyword))))]
    (when (not-empty extras)
      (throw (ex-info (format "Extra fields '%s' found during put."
                              (clojure.string/join ", " extras))
                      e)))))

(defn put-async
  "Try not to use me unless you absolutely have to. Prefer `put` (synchronous)."
  [e restricted-keys]
  (validate-put! e restricted-keys)
  (let [raw (select-keys e (conj restricted-keys
                                 :crux.db/id :type
                                 (support/typify e :updated-at)
                                 (support/typify e :published-at)))
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

(defn reify-results
  ([getter r]
   (map #(-> % first getter) r)))

(defn query-raw
  ([q]
   (->> (crux/q (crux/db crux-node) q)
        (reify-results get*)))
  ([q param]
   (->> (crux/q (crux/db crux-node) q param)
        (reify-results get*))))

(defn query
  ([q]
   (->> (crux/q (crux/db crux-node) q)
        (reify-results get)))
  ([q param]
   (->> (crux/q (crux/db crux-node) q param)
        (reify-results get))))

;; (defn list
;;   [type]
;;   (let [timestamp-field (support/namespace-kw type :updated-at)
;;         list-query {:find     '[e updated-at]
;;                     :where    ['[e :type type]
;;                                 ['e timestamp-field 'updated-at]]
;;                     :order-by '[[updated-at :desc]]
;;                     :in [type tsf]}]
;;     (query list-query type )))

(defn list
  [type]
  (let [timestamp-field (support/namespace-kw type :updated-at)
        list-query {:find     '[e updated-at]
                    :where    ['[e :type type]
                               ['e timestamp-field 'updated-at]]
                    :order-by '[[updated-at :desc]]
                    :in '[type]}]
    (->> (crux/q (crux/db crux-node)
                 list-query
                 type)
        (reify-results get))))
