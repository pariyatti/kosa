(ns dev.repl
  (:require [kosa.config :as config]
            [kosa.core :as core]
            [kosa.server :as server]
            [mount.core :as mount]))

(def dev-opts {:options {:config-file "config/config.dev.edn"}})
(def test-opts {:options {:config-file "config/config.test.edn"}})

(defn start!
  "Behaves like `-main` and provides default dev command line opts."
  ([] (start! dev-opts))
  ([opts]
   (core/mount-init!)
   (core/start opts)))

(defn stop! []
  (core/stop))

(defn restart!
  "Restart with the given opts (config) or default to dev."
  ([] (restart! dev-opts))
  ([opts]
   (stop!)
   (start! opts)))

(defn dev-mode! []
  (restart! dev-opts))

(defn test-mode! []
  (restart! test-opts))

(defn current []
  (prn "current config is: " config/config))
