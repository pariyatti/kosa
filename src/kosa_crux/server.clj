(ns kosa-crux.server
  (:require [mount.core :refer [defstate]]
            [ring.adapter.jetty :as jetty]
            [ring.logger :refer [wrap-log-request-start wrap-log-response wrap-log-request-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [reitit.ring :as rring]
            [kosa-crux.config :as config]
            [kosa-crux.routes :as routes]
            [kosa-crux.middleware :refer [wrap-router wrap-println-request]]))

(def server)

(defn handler []
  (rring/ring-handler routes/router
                      routes/default-handler))

(def app
  (-> (handler)
      wrap-router
      wrap-log-response
      (wrap-log-request-params {:transform-fn #(assoc % :level :info)})
      wrap-keyword-params
      wrap-params
      wrap-multipart-params
      wrap-json-body
      wrap-json-params
      wrap-json-response
      wrap-log-request-start))

(defn start-server! []
  (jetty/run-jetty app
                   {:port (:port config/config)
                    :join? false}))

(defn stop-server! []
  (.stop server))

(defstate server
  :start (start-server!)
  :stop (stop-server!))
