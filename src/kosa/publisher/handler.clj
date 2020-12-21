(ns kosa.publisher.handler
  (:require [kosa.publisher.views :as views]
            [ring.util.response :as resp]))

(defn index [request]
  (resp/response
   (views/index request)))
