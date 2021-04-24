(ns kuti.support.collections
  (:require [clojure.set]))

(defn find-all-nested
  [m k]
  (->> (tree-seq map? vals m)
       (filter map?)
       (keep k)))

(defn find-first [f coll]
  (first (filter f coll)))

(defn only [coll]
  (if (= 1 (count coll))
    (first coll)
    (throw (Exception. (format "Collection '%s' does not contain exactly one item." coll)))))

(defn contains-kv?
  ([m kv]
   (apply (partial contains-kv? m) kv))
  ([m k v]
   (if-let [kv (find m k)]
     (= (val kv) v)
     false)))

(defn merge-kvs [left right]
  (seq (merge (into {} left)
              (into {} right))))

(defn subset-kvs? [maybe-sub maybe-super]
  (clojure.set/subset? (set maybe-sub)
                       (set maybe-super)))
