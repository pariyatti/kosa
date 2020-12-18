(ns kosa-crux.publisher.handler
  (:require [ring.util.response :as resp]
            [kosa-crux.publisher.views :as views]))

(defn index [request]
  (resp/response
   (views/index request)))
