(ns kuti.record.core
  (:refer-clojure :exclude [get list])
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]
            [xtdb.rocksdb :as rocks]
            [kosa.config :as config]
            [kuti.support.types :as types]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]
            [kuti.support :refer [path-join]]
            [kuti.support.digest :refer [uuid ->uuid]]
            [mount.core :refer [defstate]]
            [kuti.support.time :as time]
            [clojure.string :as clojure.string]))

(def meta-keys #{:xt/id :kuti/type})
(def timestamp-keys #{:created-at :updated-at :published-at})

(def xtdb-node)
(defn get-xtdb-node []
  xtdb-node)

(defn- data-dir []
  (get-in config/config [:db-spec :data-dir]))

(defn- xtdb-http-port []
  (get-in config/config [:db-spec :xtdb-http-port]))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
	                      :db-dir      (io/file (data-dir) dir)
                        :sync?       true}})]
    (xt/start-node
     {:xtdb/tx-log              (kv-store "tx-log")
	    :xtdb/document-store      (kv-store "doc-store")
      :xtdb/index-store         (kv-store "index-store")
      :xtdb.lucene/lucene-store {:db-dir (path-join (data-dir) "lucene-dir")}
      :xtdb.http-server/server  {:port (xtdb-http-port)}})))

(defn stop-xtdb! []
  (.close xtdb-node))

(defstate xtdb-node
  :start (start-xtdb!)
  :stop  (stop-xtdb!))

(defn status []
  (xtdb.api/status xtdb-node))

(defn transact! [node txns & [error-msg]]
  (let [tx (->> txns
                (xt/submit-tx node)
                (xt/await-tx node))]
    (when-not (xt/tx-committed? node tx)
      (throw (Exception. error-msg)))))

(defn get* [id]
  (xt/entity (xt/db xtdb-node) id))

(defn get [id]
  (get* (->uuid id)))

(defn timestamp [e]
  (assoc e (types/typify e :updated-at) (time/now)))

(defn put-async* [datum]
  (xt/submit-tx xtdb-node [[::xt/put datum]]))

(defn put-prepare [raw]
  (let [old-id (:xt/id raw)]
    (-> raw
        (assoc :xt/id (if old-id
                             (->uuid old-id)
                             (uuid))))))

(defn validate-put! [e allowed-keys]
  (let [extras
        (->> meta-keys
             (apply conj allowed-keys)
             (apply dissoc e)
             (keys)
             (remove #(timestamp-keys (-> % name keyword))))]
    (when (not-empty extras)
      (throw (ex-info (format "Extra fields '%s' found during put."
                              (clojure.string/join ", " extras))
                      e)))))

(defn put-async
  "Try not to use me unless you absolutely have to. Prefer `put` (synchronous)."
  [e restricted-keys]
  (validate-put! e restricted-keys)
  (let [all-keys (concat restricted-keys
                         meta-keys
                         (map #(types/typify e %) timestamp-keys))
        raw (select-keys e all-keys)
        doc (put-prepare raw)
        tx  (put-async* doc)]
    (assoc tx :xt/id (:xt/id doc))))

(defn put [e restricted-keys]
  (let [tx   (put-async e restricted-keys)
        _    (xt/await-tx xtdb-node tx)
        card (get (:xt/id tx))]
    card))

(defn delete-async
  "Try not to use me unless you absolutely have to. Prefer `delete` (syncronous)."
  [e]
  (let [id (:xt/id e)]
    (xt/submit-tx xtdb-node [[::xt/delete id]])))

(defn delete [e]
  (let [tx   (delete-async e)
        _    (xt/await-tx xtdb-node tx)
        deleted e]
    deleted))

(defn q
  ([q]
   (xt/q (xt/db xtdb-node) q))
  ([q param]
   (xt/q (xt/db xtdb-node) q param)))

(defn reify-results
  ([getter r]
   (map #(-> % first getter) r)))

(defn query-raw
  ([q]
   (->> (xt/q (xt/db xtdb-node) q)
        (reify-results get*)))
  ([q param]
   (->> (xt/q (xt/db xtdb-node) q param)
        (reify-results get*))))

(defn query
  ([q]
   (->> (xt/q (xt/db xtdb-node) q)
        (reify-results get)))
  ([q param]
   (->> (xt/q (xt/db xtdb-node) q param)
        (reify-results get))))

(defn list
  [type]
  (let [timestamp-field (types/namespace-kw type :updated-at)
        list-query {:find     '[e updated-at]
                    :where    ['[e :kuti/type type]
                               ['e timestamp-field 'updated-at]]
                    :order-by '[[updated-at :desc]]
                    :in '[type]}]
    (query list-query type)))

(defn truncate
  [type]
  (doseq [rec (list type)]
    (delete rec)))
