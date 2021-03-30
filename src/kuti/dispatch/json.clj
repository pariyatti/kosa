(ns kuti.dispatch.json
  (:require [cheshire.generate]
            [cognitect.transit]
            [muuntaja.core :as muuntaja])
  (:import [java.time Instant]))

(extend-protocol cheshire.generate/JSONable
  java.time.Instant
  (to-json [dt gen]
    (cheshire.generate/write-string gen (str dt))))

(extend-protocol cheshire.generate/JSONable
  clojure.lang.ExceptionInfo
  (to-json [ei gen]
    (cheshire.generate/write-string gen (Throwable->map ei))))

(def time-transit-writer
  (cognitect.transit/write-handler
   (constantly "t")
   (fn [^java.time.Instant inst]
     (.format
      (com.cognitect.transit.impl.AbstractParser/getDateTimeFormat)
      (java.util.Date/from inst)))))

(def time-transit-writers
  {java.time.Instant time-transit-writer})

(def muuntaja-instance
  (muuntaja/create
   (update-in
    muuntaja/default-options
    [:formats "application/transit+json"]
    merge { ;; :decoder-opts {:handlers transit-dates/readers}
           :encoder-opts {:handlers time-transit-writers}})))
