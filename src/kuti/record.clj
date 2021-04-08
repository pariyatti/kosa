(ns kuti.record
  (:require [kuti.record.core :as core]
            [kuti.record.schema :as schema]
            [kuti.support.time :as time])
  (:refer-clojure :exclude [get list]))

(defn timestamp [e]
  (core/timestamp e))

(defn publish
  ([e]
   (core/publish e))
  ([e ts]
   (core/publish e ts)))

(defn draft [e]
  (core/draft e))

(defn get [id]
  (core/get id))

(defn put [e restricted-keys]
  (core/put e restricted-keys))

(defn delete [e]
  (core/delete e))

(defn query
  ([q] (core/query q))
  ([q param] (core/query q param)))

(defn list [type]
  (core/list type))

(defn save! [e]
  (schema/save! e))
