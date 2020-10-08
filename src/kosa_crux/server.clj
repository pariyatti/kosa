(ns kosa-crux.server
  (:require [mount.core :refer [defstate]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [reitit.ring :as rring]
            [kosa-crux.config :as config]
            [kosa-crux.routes :as routes]
            [kosa-crux.middleware :refer [wrap-router]]))

(def server)

(defn handler []
  (rring/ring-handler routes/router
                      routes/default-handler))

(def app
  (-> (handler)
      wrap-router
      wrap-keyword-params
      wrap-params
      (wrap-resource "public")
      wrap-json-body
      wrap-json-params
      wrap-json-response))

(defn start-server! []
  (jetty/run-jetty app
                   {:port (:port config/config)
                    :join? false}))

(defn stop-server! []
  (.stop server))

(defstate server
  :start (start-server!)
  :stop (stop-server!))
