(ns dev.repl
  (:require [mount.core :as mount]
            [kosa-crux.server :as server]
            [kosa-crux.core :as core]
            [kosa-crux.config :as config]))

(defn start!
  "Behaves like `-main` and provides default dev command line opts."
  []
  (core/mount-init!)
  (core/start {:options {:config-file "config/config.dev.edn"}}))

(defn stop! []
  (core/stop))

(defn restart! []
  (core/restart))

(defn load-config!
  ([] (load-config! "config/config.dev.edn"))
  ([config-file] (core/load-config! config-file)))
