(ns dev.repl
  (:require [kutis.record :as record]
            [kosa.config :as config]
            [kosa.core :as core]
            [kosa.server :as server]
            [joplin.repl]
            [joplin.alias :refer [*load-config*]]
            [mount.core :as mount]
            [kosa.library.artefacts.image.db :as image]))

(def joplin-config (*load-config* "joplin/config.edn"))

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

(defn migrate []
  (joplin.repl/migrate joplin-config :dev))

(defn seed []
  (joplin.repl/seed joplin-config :dev))

(defn current []
  (prn "current config is: " config/config))
