(ns kuti.support.collections)

(defn find-all-nested
  [m k]
  (->> (tree-seq map? vals m)
       (filter map?)
       (keep k)))

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
