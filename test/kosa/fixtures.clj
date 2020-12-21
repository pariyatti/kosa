(ns kosa.fixtures
  (:require [clojure.java.io :as io]
            crux.api
            [kosa.config :as config]
            [kosa.crux :as crux]
            [mount.core :as mount]))

(defn rm-rf
  "Recursively delete a directory."
  [^java.io.File file & [silently]]
  (when (.isDirectory file)
    (doseq [file-in-dir (.listFiles file)]
      (rm-rf file-in-dir)))
  (io/delete-file file silently))

(defn get-test-config []
  {:options {:config-file (or (System/getenv "TEST_CONFIG_FILE")
                              "config/config.test.edn")}})

(defn start-test-config []
  (mount/stop #'config/config)
  (-> (mount/with-args (get-test-config))
      (mount/only #{#'config/config})
      mount/start))

(defn start-test-db []
  (-> (mount/with-args (get-test-config))
      (mount/only #{#'config/config #'crux/crux-node})
      mount/start))

(defn reset-db! []
  (start-test-config)
  (let [data-dir (get-in config/config [:db-spec :data-dir])
        crux-log (io/file data-dir "event-log")
        crux-idx (io/file data-dir "indexes")]
    (rm-rf crux-log true)
    (rm-rf crux-idx true)))

(defn throw-lock-error []
  (let [msg "RocksDB is locked. Do you have a repl connected somewhere?"]
    (prn msg)
    (throw (ex-info msg {}))))

(defn load-states [t]
  (mount/stop #'crux/crux-node)
  ;; TODO: this is unbelievably janky... there has to be a better way.
  (reset-db!)
  (try (start-test-db)
       (t)
       (catch org.rocksdb.RocksDBException e
         (throw-lock-error))
       (catch java.lang.RuntimeException e
         (throw-lock-error)))
  ;; TODO: stopping the crux node like this saves the repl but breaks
  ;;       the browser. you can't run tests and click-test at the same
  ;;       time with this approach.
  ;; release the connection in case we run a `lein test` on the command
  ;; line while the repl is still open:
  (mount/stop #'crux/crux-node))
