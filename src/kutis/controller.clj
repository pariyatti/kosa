(ns kutis.controller)

(defn apply-mapping
  "Maps a value from `params` to `doc` either with a supplied `[field fn]` (vector)
   form or simply by copying the field (keyword) directly."
  [doc mapping params]
  (cond
    (vector? mapping) (assoc doc
                             (first mapping)
                             ((second mapping) params))
    (keyword? mapping) (assoc doc mapping (get params mapping))
    :else (throw (java.lang.Exception. (format "Parameter mapping '%s' is not a keyword or fn." mapping)))))

(defn tag-date [params]
  (if (:published-at params)
    (:published-at params)
    (java.util.Date.)))

(defn params->doc [params mappings]
  (let [mappings (conj mappings [:published-at tag-date])]
    (reduce #(apply-mapping %1 %2 params) {} mappings)))
