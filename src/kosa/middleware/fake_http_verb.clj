(ns kosa.middleware.fake-http-verb
  (:require [reitit.ring.middleware.parameters]
            [ring.middleware.multipart-params]
            [kosa.middleware.params]))

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
         (handler (assoc request :request-method fm))    ;; replace :request-method
         (handler request))))

(def form-params
  "Need `:form-params` for HTTP verb override."
  reitit.ring.middleware.parameters/parameters-middleware)

(def multipart-params
  "Need `:multipart-params` for HTTP verb override. This wrapper explicitly does NOT
   use `reitit.ring.middleware.multipart` because the specs compiled into that
   wrapper fail for POST requests which need to be mapped into PUT/PATCH/DELETE"
  {:name ::multipart-params
   :wrap ring.middleware.multipart-params/wrap-multipart-params})

(def override
  "Wraps POST requests with `_method` fields indicating another HTTP verb. The
  `form-params` and `multipart-params` wrappers are not optional. Consumption
  should look like this:

  (rring/ring-handler routes/router
                      routes/default-handler
                      {:middleware [fake-http-verb/form-params
                                    fake-http-verb/multipart-params
                                    fake-http-verb/override]})"
  {:name ::http-verb-override
   :wrap wrap-http-verb-method})
