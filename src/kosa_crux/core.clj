(ns kosa-crux.core
  (:require [mount.core :as mount]
            [kosa-crux.config :refer [config]]
            [kosa-crux.crux :refer [crux-node]]
            [kosa-crux.server :refer [server]]))

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
  "I don't do a whole lot."
  [& _args]
  (start))
