(ns kutis.record.nested
  (:require [kutis.record :as record]))

(defn attr-id [attr]
  (-> attr name (str "-id") keyword))

(defn do-to-all [f doc substr]
  (letfn [(match-substr [k]
            (clojure.string/includes? (name k) (name substr)))]
    (let [att-keys (vec (filter match-substr (keys doc)))]
      (reduce f doc att-keys))))

(defn collapse-one [doc attr]
  (let [inner (get doc attr)
        inner-id (:crux.db/id inner)]
    (-> doc
        (assoc (attr-id attr) inner-id)
        (dissoc attr))))

(defn expand-one [doc attr]
  (let [a-id (attr-id attr)
        inner (record/get (get doc a-id))]
    (-> doc
        (assoc attr inner)
        (dissoc a-id))))

(defn collapse-all
  "Finds all fields in `doc` containing `substr` and collapses them."
  [doc substr]
  (do-to-all collapse-one doc substr))

(defn expand-all
  "Finds all fields in `doc` containing `substr` and expands them."
  [doc substr]
  (do-to-all expand-one doc substr))
