(ns kutis.controller)

(defn apply-mapping [doc mapping params]
  (assoc doc
         (first mapping)
         ((second mapping) params)))

(defn tag-date [params]
  (if (:published-at params)
    (:published-at params)
    (java.util.Date.)))

(defn params->doc [params mappings]
  (let [mappings (conj mappings [:published-at tag-date])]
    (reduce #(apply-mapping %1 %2 params) {} mappings)))
