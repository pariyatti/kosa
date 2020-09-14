(ns kosa-crux.fixtures
  (:require [mount.core :as mount]
            [crux.api]
            [kosa-crux.config :refer [config]]
            [kosa-crux.crux :refer [crux-node]]))

(defn load-states [t]
  (-> (mount/only #{#'config #'crux-node})
      mount/start)
  (t))
