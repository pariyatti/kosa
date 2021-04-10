(ns kosa.mobile.today.pali-word.txt-job
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn split-pali-words [txt]
  (str/split txt #"~"))

(defn shred [entry]
  (->> (str/split entry #"—")
       (map str/trim)
       vec))

(defn ->doc [lang entry]
  {:pali (first entry)
   :translations [[lang (second entry)]]})

(defn parse [txt lang]
  (->> (split-pali-words txt)
       (map str/trim)
       (map shred)
       (map (partial ->doc lang))))

(defn ingest [f lang]
  (parse (slurp f) lang))
