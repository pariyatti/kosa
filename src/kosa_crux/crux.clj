(ns kosa-crux.crux
  (:refer-clojure :exclude [get])
  (:require [mount.core :refer [defstate]]
            [crux.api :as crux]
            [crux.rocksdb :as rocks]
            [clojure.java.io :as io]
            [kosa-crux.config :refer [config]]))

(defstate crux-node
  :start   (crux/start-node
            {:rdb {:crux/module 'crux.rocksdb/->kv-store
	                 :db-dir      (io/file (get-in config [:crux :data-dir]) "event-log")
                   :sync?       true}
	           :crux/tx-log         {:kv-store :rdb}
	           :crux/document-store {:kv-store :rdb}
             :crux/index-store    {:kv-store :rdb}})

  :stop    (.close crux-node))

(defn put [datum]
  (crux/submit-tx crux-node [[:crux.tx/put datum]]))

(defn get [id]
  (crux/entity (crux/db crux-node) id))

(defn query [q]
  (let [result-set (crux/q (crux/db crux-node) q)]
    (map #(-> % first get) result-set)))

(comment
  (put {:crux.db/id :steve :name "Steven Deobald" :place "Jammu & Kashmir"})
  (get :steve))
