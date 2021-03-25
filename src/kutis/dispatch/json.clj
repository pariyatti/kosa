(ns kutis.dispatch.json
  (:require [cheshire.generate])
  (:import [java.time Instant]))

(extend-protocol cheshire.generate/JSONable
  java.time.Instant
  (to-json [dt gen]
    (cheshire.generate/write-string gen (str dt))))
