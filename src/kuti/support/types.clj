(ns kuti.support.types
  (:require [kuti.support.debugging :refer :all]
            [kuti.support.collections :refer [find-first]]
            [clojure
             [walk :as walk]
             [string :as str]]))

(defn namespace-kw [n kw]
  (assert (keyword n))
  (assert (keyword? kw))
  (keyword (name n) (name kw)))

(defn typify [e kw]
  (if-let [t (:kuti/type e)]
    (namespace-kw t kw)
    kw))

(defn untypify [e]
  (dissoc e :kuti/type))

(defn untemplate [e]
  (let [index-kw (find-first #(= (name %) "index") (keys e))]
    (dissoc e :crux.db/id index-kw)))

(defn dup-rename [ns kw]
  (keyword (name ns) (name kw)))

(defn dup
  "[Dup]licate a model into a new ns."
  [e ns]
  (let [renamer (fn [form]
                  (if (map? form)
                    (reduce-kv (fn [acc k v] (assoc acc (dup-rename ns k) v)) {} form)
                    form))]
    (walk/postwalk renamer (-> e untypify untemplate))))
