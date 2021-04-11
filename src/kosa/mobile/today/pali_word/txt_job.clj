(ns kosa.mobile.today.pali-word.txt-job
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kosa.mobile.today.pali-word.db :as db])
  (:import [java.net URI]))

(defn split-pali-words [txt]
  (str/split txt #"~"))

(defn shred [entry]
  (->> (str/split entry #"—")
       (map str/trim)
       vec))

(defn ->doc [lang entry]
  {:pali-word/pali (first entry)
   :pali-word/translations [[lang (second entry)]]})

(defn parse [txt lang]
  (->> (split-pali-words txt)
       (map str/trim)
       (map shred)
       (map (partial ->doc lang))))

#_(defn db-insert [pali-word]
  (let [existing (db/q :pali-word/original-pali
                       (:pali-word/original-pali pali-word))]
    (log/info "Pali Word RSS: attempting insert")
    (when (= 0 (count existing))
      (db/save! (merge {:pali-word/bookmarkable true
                        :pali-word/shareable true}
                       pali-word)))))

(defn db-insert [pali-word]
  (log/info "Pali Word TXT: attempting insert")
  (db/save! (merge {:pali-word/bookmarkable true
                    :pali-word/shareable true
                    :pali-word/original-pali (:pali-word/pali pali-word)
                    :pali-word/original-url (URI. "")}
                   pali-word)))

(defn ingest [f lang]
  (doseq [word (parse (slurp f) lang)]
    (db-insert word)))
