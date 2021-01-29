(ns kosa.middleware
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :as resp]
            [reitit.ring :as rring]))

(defn wrap-spec-validation [spec handler]
  (fn [request]
    (if (s/invalid? (s/conform spec (:params request)))
      (resp/bad-request (str "Invalid parameters: " (s/explain-str spec (:params request))
                             "\n\n"
                             "Request was: "
                             (with-out-str (clojure.pprint/pprint request))))
      (handler request))))

(defn wrap-println-request
  "Just some lazy debugging trash"
  [h]
  (fn [req]
    (prn "REQUEST:")
    (prn req)
    (h req)))

(defn- http-verb-method
  "From https://github.com/metosin/reitit/blob/master/doc/ring/RESTful_form_methods.md"
  [request]
  (some-> (or (get-in request [:form-params "_method"])         ;; look for "_method" field in :form-params
              (get-in request [:multipart-params "_method"]))   ;; or in :multipart-params
          clojure.string/lower-case
          keyword))

(defn wrap-http-verb-method
  ;; TODO: write a test that ensures the output of `wrap-http-verb-method` is lower-case
  [handler]
  (fn [request]
    (if-let [fm (and (= :post (:request-method request)) ;; if this is a :post request
                     (http-verb-method request))]        ;; and there is a "_method" field
         (handler (assoc request :request-method fm)) ;; replace :request-method
         (handler request))))

(defn wrap-path-params
  [handler]
  (fn [request]
    (prn "request/path-params:")
    (prn (:path-params request))
    (if-let [id (-> request :path-params :id)]
      (handler (assoc-in request [:params :crux.db/id] id))
      (handler request))))

(def http-verb-override
  "Wraps POST requests with `_method` fields indicating another HTTP verb."
  {:name ::http-verb-override
   :wrap wrap-http-verb-method})

(def path-params
  "Injects `:path-params` into `:params` for specific keys (currently: `:id`)."
  {:name ::path-params
   :wrap wrap-path-params})
