(ns kosa.server
  (:require [kosa.config :as config]
            [kosa.middleware.fake-http-verb :as fake-http-verb]
            [kosa.routes :as routes]
            [mount.core :refer [defstate]]
            [reitit.ring :as rring]
            [ring.adapter.jetty :as jetty]))

(def server)

(def app-handler
  (rring/ring-handler routes/router
                      routes/default-handler
                      {:middleware [fake-http-verb/form-params
                                    fake-http-verb/multipart-params
                                    fake-http-verb/override]
                       :inject-router? true}))

(defn start-server! []
  (jetty/run-jetty #'app-handler
                   {:port (:port config/config)
                    :join? false}))

(defn stop-server! []
  (.stop server))

(defstate server
  :start (start-server!)
  :stop (stop-server!))
