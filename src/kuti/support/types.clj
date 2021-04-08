(ns kuti.support.types)

(defn namespace-kw [n kw]
  (assert (keyword n))
  (assert (keyword? kw))
  (keyword (name n) (name kw)))

(defn typify [e kw]
  (if-let [t (:type e)]
    (namespace-kw t kw)
    kw))
