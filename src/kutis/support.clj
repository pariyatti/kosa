(ns kutis.support
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn kutis-root
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
