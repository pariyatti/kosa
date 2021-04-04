(ns kuti.fixtures.record-fixtures
  (:require [clojure.java.io :as io]
            [kosa.config :as config]
            [kuti.support.debugging :refer :all]
            [kuti.record :as db]
            [kuti.record.core :as db-core]
            [dev.repl]
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
      (mount/only #{#'config/config #'db-core/crux-node})
      mount/start))

(defn reset-db! []
  (start-test-config)
  (let [data-dir (get-in config/config [:db-spec :data-dir])
        _ (when-not (= "data/test/" data-dir)
            (throw (ex-info "Config [:db-spec :data-dir] is NOT 'data/test/'. Aborting.")))
        db-log (io/file data-dir "tx-log")
        db-idx (io/file data-dir "index-store")
        db-doc (io/file data-dir "doc-store")
        db-luc (io/file data-dir "lucene-dir")]
    (rm-rf db-log true)
    (rm-rf db-idx true)
    (rm-rf db-doc true)
    (rm-rf db-luc true)))

(defn throw-lock-error []
  (let [msg "RocksDB is locked. Do you have a repl connected somewhere?"]
    (prn msg)
    (throw (ex-info msg {}))))

(defn load-states
  "TODO: This is fundamentally broken. `kuti` should not depend on
         kosa _at all_ which means `record-fixtures` should create its
         own Crux node. This would also solve the jankiness in this fn."
  [t]
  (mount/stop #'db-core/crux-node)
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
  (mount/stop #'db-core/crux-node))

(defn force-destroy-db
  [t]
  (mount/stop #'db-core/crux-node)
  (reset-db!)
  (mount/stop #'db-core/crux-node)
  (t))

(defn force-migrate-db
  [t]
  (mount/stop #'db-core/crux-node)
  ;; TODO: don't rely on `dev.repl`
  (dev.repl/migrate :test)
  (mount/stop #'db-core/crux-node)
  (t))

(defn force-start-db
  [t]
  (try (start-test-db)
       (t)
       (catch org.rocksdb.RocksDBException e
         (throw-lock-error))
       (catch java.lang.RuntimeException e
         (throw-lock-error))
       (finally
         (mount/stop #'db-core/crux-node))))
