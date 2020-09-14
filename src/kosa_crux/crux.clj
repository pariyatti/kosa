(ns kosa-crux.crux
  (:refer-clojure :exclude [get])
  (:require [mount.core :refer [defstate]]
            [crux.api :as crux]
            [crux.kv.rocksdb :as rocks]
            [clojure.java.io :as io]
            [kosa-crux.config :refer [config]]))

(defstate crux-node
  :start   (crux/start-node
            {:crux.node/topology                 '[crux.standalone/topology
	                                           crux.kv.rocksdb/kv-store]
	     :crux.standalone/event-log-dir      (io/file (get-in config [:crux :data-dir]) "event-log")
	     :crux.standalone/event-log-kv-store 'crux.kv.rocksdb/kv
	     :crux.kv/db-dir                     (io/file (get-in config [:crux :data-dir]) "indexes")})
  :stop (.close crux-node))

(defn insert [datum]
  (crux/submit-tx crux-node [[:crux.tx/put datum]]))

(defn get [id]
  (crux/entity (crux/db crux-node) id))

(comment
  (insert {:crux.db/id :steve :name "Steven Deobald" :place "Jammu & Kashmir"})
  (get :steve))
