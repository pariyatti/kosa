(ns kosa.middleware.params
  (:require [ring.middleware.keyword-params]
            [ring.middleware.multipart-params]))

(defn wrap-wrap-keyword-params [handler]
  (ring.middleware.keyword-params/wrap-keyword-params
   handler
   {:parse-namespaces? true}))

(def keyword-params
  {:name ::keyword-params
   :wrap wrap-wrap-keyword-params})

(def multipart-params
  {:name ::multipart-params
   :wrap ring.middleware.multipart-params/wrap-multipart-params})

(defn wrap-path-params
  [handler]
  (fn [request]
    (if-let [id (-> request :path-params :id)]
      (handler (assoc-in request [:params :xt/id] id))
      (handler request))))

(def path-params
  "Injects `:path-params` into `:params` for specific keys (currently: `:id`)."
  {:name ::path-params
   :wrap wrap-path-params})
