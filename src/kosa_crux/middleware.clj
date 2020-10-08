(ns kosa-crux.middleware
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :as resp]
            [reitit.ring :as rring]))

(defn wrap-spec-validation [spec handler]
  (fn [request]
    (if (s/invalid? (s/conform spec (:params request)))
      (resp/bad-request (str "Invalid parameters: " (s/explain-str spec (:params request))))
      (handler request))))

(defn wrap-router [h]
  (let [router (rring/get-router h)]
    (fn [req]
      (h (assoc req :router router)))))
