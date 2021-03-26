(ns kosa.middleware
  (:require
   [reitit.ring.middleware.dev]
   [kosa.middleware.flash :as flash]
   [kosa.middleware.json :as json]
   [kosa.middleware.logger :as logger]
   [kosa.middleware.params :as params]
   [kosa.middleware.exception :as exception]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]))

(def router-bundle
  ;; reference: https://github.com/metosin/reitit/blob/master/examples/ring-swagger/src/example/server.clj
  [logger/log-request-start-middleware
   json/json-response
   json/json-params
   json/json-body
   parameters/parameters-middleware
   params/multipart-params
   muuntaja/format-middleware
   exception/exception-middleware
   coercion/coerce-request-middleware
   params/keyword-params
   params/path-params
   flash/session
   flash/flash
   logger/log-request-params-middleware
   logger/log-response-middleware])
