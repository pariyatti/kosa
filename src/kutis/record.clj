(ns kutis.record
  (:refer-clojure :exclude [get list])
  (:require [clojure.java.io :as io]
            [crux.api :as crux]
            [crux.rocksdb :as rocks]
            [kosa.config :as config]
            [kutis.support :refer [path-join]]
            [kutis.support.digest :refer [uuid ->uuid]]
            [mount.core :refer [defstate]]))

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
  (if (:crux.db/id raw)
    (assoc raw :crux.db/id (->uuid (:crux.db/id raw)))
    (assoc raw :crux.db/id (uuid))))

(defn validate-put! [e allowed-keys]
  (let [allowed-keys* (conj allowed-keys :crux.db/id :published-at)
        extras (apply dissoc e allowed-keys*)]
    (when (not-empty extras)
      (throw (ex-info (format "Extra fields '%s' found during put."
                              (clojure.string/join ", " (keys extras)))
                      e)))))

(defn put-async
  "Try not to use me unless you absolutely have to. Prefer `put` (synchronous)."
  [e restricted-keys]
  (validate-put! e restricted-keys)
  (let [raw (select-keys e (conj restricted-keys :crux.db/id))
        doc (put-prepare raw)
        tx  (put-async* doc)]
    (assoc tx :crux.db/id (:crux.db/id doc))))

;; TODO: `put` should really behave like a regular db insert wrt
;;       keys/schema -- this should throw an exception if `e` is badly formed.
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
        list-query '{:find     [e modified-at]
                     :where    [[e :type type]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]
                     :in [type]}]
    (query list-query type-kw)))
