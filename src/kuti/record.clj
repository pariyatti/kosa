(ns kuti.record
  (:require [kuti.record.core :as core]
            [kuti.record.schema :as schema]
            [kuti.record.publishing :as publishing])
  (:refer-clojure :exclude [get list]))

(defn timestamp [e]
  (core/timestamp e))

(defn publish-at [e ts]
  (publishing/publish-at e ts))

(defn publish [e]
  (publishing/publish e))

(defn republish [e]
  (publishing/republish e))

(defn draft [e]
  (publishing/draft e))

(defn get [id]
  (core/get id))

(defn put [e restricted-keys]
  (core/put e restricted-keys))

(defn delete [e]
  (core/delete e))

(defn q
  ([q] (core/q q))
  ([q param] (core/q q param)))

(defn query
  ([q] (core/query q))
  ([q param] (core/query q param)))

(defn list [type]
  (core/list type))

(defn save! [e]
  (schema/save! e))
