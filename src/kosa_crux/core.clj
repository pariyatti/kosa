(ns kosa-crux.core
  (:require [mount.core :as mount]
            [kosa-crux.config :as config]
            [kosa-crux.crux :as crux]))

(defn start []
  (-> (mount/only #{#'config/config
                    #'crux/crux-node})
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
