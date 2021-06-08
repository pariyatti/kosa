(ns kuti.support
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.algo.monads :refer [domonad maybe-m]]))

(defn kuti-root
  "Returns the root directory for the running service"
  []
  (.getCanonicalPath (io/file "./resources" "..")))

(defn path-join
  "Join any number of path fragments"
  [p & ps]
  (-> p
      (java.nio.file.Paths/get (into-array String ps))
      .normalize
      str))

(defmethod @#'io/do-copy [String String] [in out opts]
  (apply io/copy (io/file in) (io/file out) opts))

(defmacro when-let* [binding & body]
  (let [body (cons 'do body)]
   `(domonad maybe-m ~binding ~body)))

(defn assoc-unless [m k v]
  (if (get m k)
    m
    (assoc m k v)))
