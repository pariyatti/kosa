(ns kuti.record
  (:require [kuti.record.core :as core]
            [kuti.record.schema :as schema])
  (:refer-clojure :exclude [get list]))

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
