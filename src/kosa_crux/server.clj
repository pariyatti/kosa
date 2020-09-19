(ns kosa-crux.server
  (:require [mount.core :refer [defstate]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [bidi.ring :as bidi]
            [kosa-crux.config :refer [config]]
            [kosa-crux.routes :refer [routes]]))

(defn handler []
  (bidi/make-handler routes))

(defn app []
  (-> (handler)
      wrap-keyword-params
      wrap-params
      wrap-json-body
      wrap-json-params
      wrap-json-response))

(defstate server
  :start (jetty/run-jetty (app)
          {:port (-> config :server :port) :join? false})
  :stop (.stop server))
