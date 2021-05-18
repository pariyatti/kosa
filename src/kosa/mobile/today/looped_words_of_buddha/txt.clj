(ns kosa.mobile.today.looped-words-of-buddha.txt
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]
            [kuti.support.collections :refer [merge-kvs subset-kvs?]]
            [kosa.mobile.today.looped-words-of-buddha.db :as db]
            [kosa.mobile.today.looped.txt :as txt])
  (:import [java.net URI]))

(defn shred [marker entry]
  (->> (str/split entry (re-pattern marker))
       (map str/trim)
       vec))

(defn repair [marker pair]
  [(first pair) (str marker (second pair))])

(defn ->audio-url [url]
  (-> (str/split url #": ")
      second
      (URI.)))

(defn shred-cite-block [s]
  (let [cite (str/split s (re-pattern "\n"))]
    #:looped-words-of-buddha{:citation     (get cite 0)
                             :citation-url (URI. (get cite 1))
                             :store-title  (get cite 2)
                             :store-url    (URI. (or (get cite 3) ""))}))

(defn shred-blocks [lang v]
  (let [blocks (str/split (second v) (re-pattern "\n\n"))
        cite-block (shred-cite-block (get blocks 2))]
    (merge
     #:looped-words-of-buddha{:words (first v)
                              :audio-url (->audio-url (get blocks 0))
                              :translations [[lang (get blocks 1)]]}
     cite-block)))

(defn find-existing [words]
  (first (db/q :looped-words-of-buddha/words (:looped-words-of-buddha/words words))))

(defn db-insert* [words]
  (db/save! (merge #:looped-words-of-buddha
                   {:bookmarkable true
                    :shareable true
                    :original-words (:looped-words-of-buddha/words words)
                    :original-url (URI. "")}
                   words)))

(defn db-insert! [words-of-buddha]
  (if-let [existing (find-existing words-of-buddha)]
    (let [merged (merge-kvs (:looped-words-of-buddha/translations existing)
                            (:looped-words-of-buddha/translations words-of-buddha))]
      (if (= merged (:looped-words-of-buddha/translations existing))
        (log/info (format "Duplicate words ignored: %s" (:looped-words-of-buddha/words words-of-buddha)))
        (do
          (log/info (format "Merging translations: %s" (:looped-words-of-buddha/words words-of-buddha)))
          (db-insert* (assoc existing :looped-words-of-buddha/translations merged)))))
    (db-insert* words-of-buddha)))

(defn parse [txt lang]
  (let [marker (get {"en" "Listen"
                     "es" "Escuchar"
                     "fr" "Ecouter " ;; NOTE: yes, it contains a space
                     "it" "Ascolta"
                     "pt" "Ouça"
                     "sr" "Slušaj"
                     "zh" "Listen"} lang)
        m (str marker ": ")]
    (->> (txt/split-file txt)
         (map str/trim)
         (map #(shred m %))
         (map #(repair m %))
         (map #(shred-blocks lang %)))))
