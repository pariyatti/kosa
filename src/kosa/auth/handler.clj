(ns kosa.auth.handler
  (:require [kosa.auth.views :as views]
            [ring.util.response :as resp]))

(defn login [request]
  (resp/response
   (views/login request)))
