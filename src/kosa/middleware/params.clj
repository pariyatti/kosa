(ns kosa.middleware.params
  (:require [ring.middleware.keyword-params]
            [ring.middleware.multipart-params]))

(def keyword-params
  {:name ::keyword-params
   :wrap ring.middleware.keyword-params/wrap-keyword-params})

(def multipart-params
  {:name ::multipart-params
   :wrap ring.middleware.multipart-params/wrap-multipart-params})

(defn wrap-path-params
  [handler]
  (fn [request]
    (if-let [id (-> request :path-params :id)]
      (handler (assoc-in request [:params :crux.db/id] id))
      (handler request))))

(def path-params
  "Injects `:path-params` into `:params` for specific keys (currently: `:id`)."
  {:name ::path-params
   :wrap wrap-path-params})
