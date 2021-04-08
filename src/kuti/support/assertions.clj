(ns kuti.support.assertions
  (:import [java.lang IllegalArgumentException]))

(defn assert-type-is-keyword [e]
  (when-not (-> e :type keyword?)
    (throw (IllegalArgumentException. ":type key must be a keyword."))))
