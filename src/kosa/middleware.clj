(ns kosa.middleware
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

(defn wrap-println-request
  "Just some lazy debugging trash"
  [h]
  (fn [req]
    (prn "REQUEST:")
    (prn req)
    (h req)))

(defn- hidden-method
  "From https://github.com/metosin/reitit/blob/master/doc/ring/RESTful_form_methods.md"
  [request]
  (some-> (or (get-in request [:form-params "_method"])         ;; look for "_method" field in :form-params
              (get-in request [:multipart-params "_method"]))   ;; or in :multipart-params
          clojure.string/lower-case
          keyword))

(defn wrap-hidden-method
  ;; TODO: write a test that ensures the output of `wrap-hidden-method` is lower-case
  ;; TODO: move this (and all other middleware) into reitit middleware wrappers
  [handler]
  (fn [request]
    (if-let [fm (and (= :post (:request-method request)) ;; if this is a :post request
                     (hidden-method request))] ;; and there is a "_method" field
         (handler (assoc request :request-method fm)) ;; replace :request-method
         (handler request))))
