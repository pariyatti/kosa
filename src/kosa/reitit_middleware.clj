(ns kosa.reitit-middleware)

(defn- hidden-method
  "From https://github.com/metosin/reitit/blob/master/doc/ring/RESTful_form_methods.md"
  [request]
  (some-> (or (get-in request [:form-params "_method"])         ;; look for "_method" field in :form-params
              (get-in request [:multipart-params "_method"]))   ;; or in :multipart-params
          clojure.string/lower-case
          keyword))

(def wrap-hidden-method
  {:name ::wrap-hidden-method
   :wrap (fn [handler]
           (fn [request]
             (if-let [fm (and (= :post (:request-method request)) ;; if this is a :post request
                              (hidden-method request))]           ;; and there is a "_method" field
               (handler (assoc request :request-method fm)) ;; replace :request-method
               (handler request))))})
