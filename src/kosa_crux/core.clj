(ns kosa-crux.core
  (:require [mount.core :as mount]
            [clojure.tools.logging :refer [info]]
            [kosa-crux.config :refer [config]]
            [kosa-crux.crux :refer [crux-node]]
            [kosa-crux.server :refer [server]])
  (:gen-class))

(defn start []
  (-> (mount/only #{#'config
                    #'crux-node
                    #'server})
      (mount/start)))

(defn stop []
  (mount/stop))

(defn restart []
  (stop)
  (start))

(defn -main
  [& _args]
  (info "Starting the server")
  (start))
