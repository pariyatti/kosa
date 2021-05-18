(ns kosa.mobile.today.looped-pali-word.txt
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]
            [kuti.support.collections :refer [merge-kvs subset-kvs?]]
            [kosa.mobile.today.looped.txt :as txt]
            [kosa.mobile.today.looped-pali-word.db :as db])
  (:import [java.net URI]))

(defn shred [entry]
  (->> (str/split entry #"—")
       (map str/trim)
       vec))

(defn ->doc [lang entry]
  {:looped-pali-word/pali (first entry)
   :looped-pali-word/translations [[lang (second entry)]]})

(defn parse [txt lang]
  (->> (txt/split-file txt)
       (map str/trim)
       (map shred)
       (map (partial ->doc lang))))

#_(defn db-insert [pali-word]
  (let [existing (db/q :looped-pali-word/original-pali
                       (:looped-pali-word/original-pali pali-word))]
    (log/info "Pali Word RSS: attempting insert")
    (when (= 0 (count existing))
      (db/save! (merge {:looped-pali-word/bookmarkable true
                        :looped-pali-word/shareable true}
                       pali-word)))))

(defn find-existing [pali-word]
  (first (db/q :looped-pali-word/pali (:looped-pali-word/pali pali-word))))

(defn db-insert* [pali-word]
  (db/save! (merge {:looped-pali-word/bookmarkable true
                    :looped-pali-word/shareable true
                    :looped-pali-word/original-pali (:looped-pali-word/pali pali-word)
                    :looped-pali-word/original-url (URI. "")}
                   pali-word)))

(defn db-insert! [pali-word]
  (if-let [existing (find-existing pali-word)]
    (let [merged (merge-kvs (:looped-pali-word/translations existing)
                            (:looped-pali-word/translations pali-word))]
      (if (= merged (:looped-pali-word/translations existing))
        (log/info (format "Duplicate word ignored: %s" (:looped-pali-word/pali pali-word)))
        (do
          (log/info (format "Merging translations: %s" (:looped-pali-word/pali pali-word)))
          (db-insert* (assoc existing :looped-pali-word/translations merged)))))
    (db-insert* pali-word)))

(defn ingest [f lang]
  (log/info (format "Pali Word TXT: started ingesting file '%s' for lang '%s'" f lang))
  (let [words (parse (slurp f) lang)
        word-count (count words)]
    (log/info (format "Processing %s pali words from TXT." word-count))
    (doseq [[n word] (map-indexed #(vector %1 %2) words)]
      (log/info (format "Attempting insert of %s / %s" (+ n 1) word-count))
      (-> word
          ;; (download-attachments!) ;; there is no audio for looped pali words
          (db-insert!))))
  (log/info (format "Pali Word TXT: done ingesting file '%s' for lang '%s'" f lang)))
