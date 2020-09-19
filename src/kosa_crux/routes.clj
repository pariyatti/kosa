(ns kosa-crux.routes
  (:require [ring.util.response :as resp]))

(defn not-found [_request]
  (resp/not-found {:message "not-found"}))

(defn pong [_request]
  (resp/response "pong"))

(def routes
  ["/" [["ping" pong]
        [true not-found]]])
