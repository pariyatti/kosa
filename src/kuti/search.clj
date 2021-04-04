(ns kuti.search
  (:require [clojure.string :as clojure.string]
            [kuti.support.debugging :refer :all]))

(defn make-searchable [s]
  (as-> (clojure.string/split s #"-|_|~|=|\$|\{|\}|\.|\[|\]|\+") searchables
    (conj searchables s)
    (clojure.string/join " " searchables)))

(defn tag-searchables [e string]
  (assert (not (nil? (:type e)))
          "Entity must contain :type key to tag searchables.")
  ;; TODO: clean this up
  (let [k (keyword (format "%s/searchables" (name (:type e))))
        old (get e k)
        built (make-searchable string)
        found (when old (clojure.string/includes? old built))
        new (-> old
                (str " " (when-not found built))
                clojure.string/trim)]
    (assoc e k new)))
