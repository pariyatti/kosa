(ns kutis.record.nested)

(defn collapse-one [doc attr]
  (let [attr-id (-> attr name (str "-id") keyword)
        inner (get doc attr)
        inner-id (:crux.db/id inner)]
    (-> doc
        ;; TODO: pull this out
        ;; (kutis.search/tag-searchables (:filename inner))
        (dissoc attr)
        (assoc attr-id inner-id))))

(defn collapse-all
  "Finds all fields in `doc` containing `substr` and collapses them."
  [doc substr]
  (letfn [(match-substr [k]
            (clojure.string/includes? (name k) substr))]
    (let [att-keys (vec (filter match-substr (keys doc)))]
      (reduce collapse-one doc att-keys))))
