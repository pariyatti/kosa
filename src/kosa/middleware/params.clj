(ns kosa.middleware.params
  (:require [ring.middleware.keyword-params]
            [ring.middleware.multipart-params]))

(def keyword-params
  {:name ::keyword-params
   :wrap ring.middleware.keyword-params/wrap-keyword-params})

(def multipart-params
  {:name ::multipart-params
   :wrap ring.middleware.multipart-params/wrap-multipart-params})
