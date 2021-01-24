(ns kosa.mobile.handler
  (:require [kosa.mobile.views :as views]
            [ring.util.response :as resp]))

(defn index [request]
  (resp/response
   (views/index request)))
