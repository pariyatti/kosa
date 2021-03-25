(ns kutis.support.debugging
  (:require [clojure.pprint :refer [pprint]]))

(defn dbg
  "Provides simple indirection to avoid
   `println`, `prn`, and friends.
   Easier to grep."
  [& s]
  (doseq [each s]
    (pprint each)))
