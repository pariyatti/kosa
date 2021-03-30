(ns kuti.record.nested
  (:require [kuti.record :as record]
            [clojure.string]))

(defn field->id [attr]
  (-> attr name (str "-id") keyword))

(defn id->field [attr-id]
  (-> attr-id name (clojure.string/replace #"-id" "") keyword))

(defn do-to-all [f doc substr]
  (letfn [(match-substr [k]
            (clojure.string/includes? (name k) (name substr)))]
    (let [att-keys (vec (filter match-substr (keys doc)))]
      (reduce f doc att-keys))))

(defn collapse-one [doc attr]
  (let [inner (get doc attr)
        inner-id (:crux.db/id inner)]
    (-> doc
        (assoc (field->id attr) inner-id)
        (dissoc attr))))

(defn expand-one [doc attr-id]
  (let [attr (id->field attr-id)
        inner (record/get (get doc attr-id))]
    (-> doc
        (assoc attr inner)
        (dissoc attr-id))))

(defn collapse-all
  "Finds all fields in `doc` containing `substr` and collapses them."
  [doc substr]
  (do-to-all collapse-one doc substr))

(defn expand-all
  "Finds all fields in `doc` containing `substr` and expands them."
  [doc substr]
  (do-to-all expand-one doc substr))
