(ns kosa.mobile.today.looped-pali-word.txt-job
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.collections :refer [merge-kvs subset-kvs?]]
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

(defn find-existing [pali-word]
  (first (db/q :pali-word/pali (:pali-word/pali pali-word))))

(defn db-insert* [pali-word]
  (db/save! (merge {:pali-word/bookmarkable true
                    :pali-word/shareable true
                    :pali-word/original-pali (:pali-word/pali pali-word)
                    :pali-word/original-url (URI. "")}
                   pali-word)))

(defn db-insert [pali-word]
  (log/info "Pali Word TXT: attempting insert")
  (if-let [existing (find-existing pali-word)]
    (let [merged (merge-kvs (:pali-word/translations existing)
                            (:pali-word/translations pali-word))]
      (when-not (= merged (:pali-word/translations existing))
        (db-insert* (assoc existing :pali-word/translations merged))))
    (db-insert* pali-word)))

(defn ingest [f lang]
  (doseq [word (parse (slurp f) lang)]
    (db-insert word)))

;; 1. lookup word by `:pali-word/pali`
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
