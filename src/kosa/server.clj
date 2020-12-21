(ns kosa.server
  (:require [kosa.config :as config]
            [kosa.middleware
             :refer
             [wrap-hidden-method wrap-println-request wrap-router]]
            [kosa.routes :as routes]
            [mount.core :refer [defstate]]
            [reitit.ring :as rring]
            [ring.adapter.jetty :as jetty]
            [ring.logger
             :refer
             [wrap-log-request-params wrap-log-request-start wrap-log-response]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.json
             :refer
             [wrap-json-body wrap-json-params wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [wrap-params]]))

(def server)

(defn handler []
  (rring/ring-handler routes/router
                      routes/default-handler
                      ;; TODO: the reitit approach to middleware makes _so much more sense_ than the ring
                      ;;       default, but it requires wrapping every ring middleware. we should do that? -sd
                      ;; {:middleware
                      ;;  [;; reitit.ring.middleware.parameters/parameters-middleware ;; needed to have :form-params in the request map
                      ;;   ;; reitit.ring.middleware.multipart/multipart-middleware   ;; needed to have :multipart-params in the request map
                      ;;   wrap-hidden-method]}
                      ))

(def app
  (-> (handler)
      wrap-router
      wrap-log-response
      (wrap-log-request-params {:transform-fn #(assoc % :level :info)})
      wrap-hidden-method
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
