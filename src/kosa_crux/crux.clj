(ns kosa-crux.crux
  (:refer-clojure :exclude [get])
  (:require [mount.core :refer [defstate]]
            [crux.api :as crux]
            [crux.rocksdb :as rocks]
            [clojure.java.io :as io]
            [kosa-crux.config :as config]))

(def crux-node)

(defn start-crux! []
  (crux/start-node
   {:rdb {:crux/module 'crux.rocksdb/->kv-store
	        :db-dir      (io/file (get-in config/config [:db-spec :data-dir]) "event-log")
          :sync?       true}
	  :crux/tx-log         {:kv-store :rdb}
	  :crux/document-store {:kv-store :rdb}
    :crux/index-store    {:kv-store :rdb}}))

(defn stop-crux! []
  (.close crux-node))

(defstate crux-node
  :start (start-crux!)
  :stop  (stop-crux!))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get [id]
  (crux/entity (crux/db crux-node) id))

(defn put-async* [datum]
  (crux/submit-tx crux-node [[:crux.tx/put datum]]))

(defn put-prepare [raw]
  (if (:crux.db/id raw)
    raw
    (assoc raw :crux.db/id (uuid))))

(defn put-async
  "Try not to use me unless you absolutely have to. Prefer `put` (synchronous)."
  [e restricted-keys]
  (let [raw (select-keys e (conj restricted-keys :crux.db/id))
        doc     (put-prepare raw)
        tx      (put-async* doc)]
    (assoc tx :crux.db/id (:crux.db/id doc))))

;; TODO: `put` should really behave like a regular db insert wrt
;;       keys/schema -- this should throw an exception if `e` is badly formed.
(defn put [e restricted-keys]
  (let [tx   (put-async e restricted-keys)
        _    (crux/await-tx crux-node tx)
        card (get (:crux.db/id tx))]
    card))

(defn delete [e]
  (let [id (:crux.db/id e)]
    (crux/submit-tx crux-node [[:crux.tx/delete id]])))

(defn query [q]
  (let [result-set (crux/q (crux/db crux-node) q)]
    (map #(-> % first get) result-set)))
