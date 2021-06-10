(ns kuti.record.nested
  (:require [kuti.record :as record]
            [clojure.string]
            [kuti.support.digest :as digest]
            [kuti.support.debugging :refer :all]))

(defn field->id [attr]
  (keyword (namespace attr)
           (-> attr name (str "-id"))))

(defn id->field [attr-id]
  (keyword (namespace attr-id)
           (-> attr-id name (clojure.string/replace #"-id" ""))))

(defn do-to-all [f doc substr]
  (letfn [(match-substr [k]
            (clojure.string/includes? (name k) (name substr)))]
    (let [att-keys (vec (filter match-substr (keys doc)))]
      (reduce f doc att-keys))))

(defn collapse-one [doc attr]
  (let [inner (get doc attr)
        inner-id (:crux.db/id inner)]
    (-> doc
        (assoc (field->id attr) (or inner-id
                                    (digest/null-uuid)))
        (dissoc attr))))

(defn expand-one [doc attr-id]
  (let [attr (id->field attr-id)
        inner (record/get (get doc attr-id))]
    (-> doc
        (assoc attr (or inner nil))
        (dissoc attr-id))))

(defn collapse-all
  "Finds all fields in `doc` containing `substr` and collapses them."
  [doc substr]
  (do-to-all collapse-one doc substr))

(defn expand-all
  "Finds all fields in `doc` containing `substr` and expands them."
  [doc substr]
  (do-to-all expand-one doc substr))
