(ns kuti.support.digest
  (:import [java.util UUID])
  (:require [clojure.string :as string]))

(defn uuid-v4
  "Default randomUUID implementation."
  []
  (java.util.UUID/randomUUID))

(defn uuid
  ([]
   (uuid-v4))
  ([s]
   (assert (string? s))
   (java.util.UUID/fromString s)))

(defn ->uuid
  "Return a UUID from UUID or String."
  [o]
  (cond
    (uuid? o)   o
    (string? o) (uuid o)
    :else       (throw (java.lang.IllegalArgumentException.
                        (str "Type '" (class o) "' cannot be parsed to UUID.")))))
