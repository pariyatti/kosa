(ns kosa-crux.library.handler
  (:require [ring.util.response :as resp]
            [kosa-crux.library.views :as views]))

(defn index [request]
  (resp/response
   (views/index request)))
