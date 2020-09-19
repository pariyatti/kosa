(ns kosa-crux.routes
  (:require [ring.util.response :as resp]
            [kosa-crux.entity.pali-word.handler :as pali-word-handler]))

(defn not-found [_request]
  (resp/not-found {:message "not-found"}))

(defn pong [_request]
  (resp/response "pong"))

(def routes
  ["/" [["ping" pong]
        ["api/v1/today.json" pali-word-handler/list]
        [true not-found]]])
