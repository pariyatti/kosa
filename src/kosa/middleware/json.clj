(ns kosa.middleware.json
  (:require [ring.middleware.json]))

(def json-body
  {:name ::json-body
   :wrap ring.middleware.json/wrap-json-body})

(def json-params
  {:name ::json-params
   :wrap ring.middleware.json/wrap-json-params})

(def json-response
  {:name ::json-response
   :wrap ring.middleware.json/wrap-json-response})
