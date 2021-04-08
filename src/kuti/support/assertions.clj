(ns kuti.support.assertions
  (:import [java.lang IllegalArgumentException]))

(defn assert-type-is-keyword [e]
  (when-not (-> e :kuti/type keyword?)
    (throw (IllegalArgumentException. ":kuti/type key must be a keyword."))))
