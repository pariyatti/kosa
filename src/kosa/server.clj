(ns kosa.server
  (:require [kosa.config :as config]
            [kosa.middleware]
            [kosa.middleware.params]
            [kosa.routes :as routes]
            [mount.core :refer [defstate]]
            [reitit.ring :as rring]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.multipart :as multipart]
            [ring.adapter.jetty :as jetty]))

(def server)

(def app-handler
  (rring/ring-handler routes/router
                      routes/default-handler
                      {:middleware
                       [parameters/parameters-middleware        ;; need :form-params for http-verb
                        kosa.middleware.params/multipart-params ;; need :multipart-params for http-verb
                        kosa.middleware/http-verb-override]
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
