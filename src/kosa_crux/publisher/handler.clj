(ns kosa-crux.publisher.handler
  (:require [ring.util.response :as resp]
            [kosa-crux.publisher.views :as views]))

(defn index [_request]
  (resp/response
   (views/index)))
