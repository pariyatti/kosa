(ns kosa.mobile.today.looped-pali-word.txt
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]
            [kuti.support.collections :refer [merge-kvs subset-kvs?]]
            [kosa.mobile.today.looped-pali-word.db :as db])
  (:import [java.net URI]))

(defn split-pali-words [txt]
  (str/split txt #"~"))

(defn shred [entry]
  (->> (str/split entry #"—")
       (map str/trim)
       vec))

(defn ->doc [lang entry]
  {:looped-pali-word/pali (first entry)
   :looped-pali-word/translations [[lang (second entry)]]})

(defn parse [txt lang]
  (->> (split-pali-words txt)
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
        (db-insert* (assoc existing :looped-pali-word/translations merged))))
    (db-insert* pali-word)))

(defn ingest [f lang]
  (log/info (format "Pali Word TXT: ingesting file '%s' for lang '%s'" f lang))
  (let [words (parse (slurp f) lang)
        word-count (count words)]
    (log/info (format "Processing %s pali words from TXT." word-count))
    (doseq [[n word] (map-indexed #(vector %1 %2) words)]
      (log/info (format "Attempting insert of %s / %s" n word-count))
      (-> word
          ;; (download-attachments!) ;; there is no audio for looped pali words
          (db-insert!)))))

;; 1. lookup word by `:looped-pali-word/pali`
;;    (a) lookup translation for the current language
;;    (b) add translation if (a) fails

;; 2. add word if (1) fails
;;    (a) lookup largest "looped" index
;;    (b) add "largest + 1" as index
;;        ...but if largest = nil, index is 0
;;    (c) download and attach audio

;; 3. daily job creates "published on today" cards at 00:00:00:
;;
;; (def looped-card-count 220)
;; (def days-since-epoch (t/days (t/between (t/epoch) (t/now))))
;; (def days-since-perl (- days-since-epoch 12902))
;; (def todays-word (mod days-since-perl looped-card-count))
