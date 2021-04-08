(ns kuti.support.types)

(defn namespace-kw [n kw]
  (assert (keyword n))
  (assert (keyword? kw))
  (keyword (name n) (name kw)))

(defn typify [e kw]
  (if-let [t (:kuti/type e)]
    (namespace-kw t kw)
    kw))
