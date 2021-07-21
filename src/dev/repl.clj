(ns dev.repl
  (:require [kuti.record :as record]
            [kosa.config :as config]
            [kosa.core :as core]
            [kosa.server :as server]
            [kuti.support.debugging :refer :all]
            [joplin.repl]
            [joplin.crux.database] ;; required for joplin to work
            [joplin.alias :refer [*load-config*]]
            [mount.core :as mount]
            [kosa.library.artefacts.image.db :as image]
            [kosa.mobile.today.looped-pali-word.txt :as txt]
            [crux.api :as x]
            [clojure.tools.logging :as log]))

(def joplin-config (*load-config* "joplin/config.edn"))

(def dev-opts {:options {:config-file "config/config.dev.edn"}})
(def test-opts {:options {:config-file "config/config.test.edn"}})
(def prod-opts {:options {:config-file "config/config.prod.edn"}})
(defn get-conf [conf]
  (get {:dev  dev-opts
        :test test-opts
        :prod prod-opts} (keyword conf)))

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

(defn current-config []
  (prn "current config is: " config/config))

(defn migrate!
  ([]
   (joplin.repl/migrate joplin-config :dev))
  ([env]
   (joplin.repl/migrate joplin-config env)))

(defn seed! []
  (joplin.repl/seed joplin-config :dev))

(defn node []
  (kuti.record.core/get-crux-node))
