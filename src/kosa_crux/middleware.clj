(ns kosa-crux.middleware
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :as resp]))

(defn wrap-spec-validation [spec handler]
  (fn [request]
    (if (s/invalid? (s/conform spec (:params request)))
      (resp/bad-request (str "Invalid parameters: " (s/explain-str spec (:params request))))
      (handler request))))
