(ns kosa.mobile.today.looped-words-of-buddha.txt
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped-words-of-buddha.db :as db]
            [kosa.mobile.today.looped.txt :as txt]
            [kuti.support.strings :as strings]
            [kuti.support.digest :as digest]
            [kuti.support.types :as types])
  (:import [java.net URI]))

(defn shred [marker entry]
  (->> (str/split entry (re-pattern marker))
       (map strings/trim!)
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
                             :store-title  (or (get cite 2) "")
                             :store-url    (URI. (or (get cite 3) ""))}))

(defn shred-blocks [lang v]
  (let [all-blocks (str/split (second v) (re-pattern "\n\n"))
        cite-block (shred-cite-block (last all-blocks))
        blocks (drop-last all-blocks)
        audio-url (first blocks)
        translation (->> blocks
                         (drop 1)
                         (str/join "\n\n"))]
    (merge
     #:looped-words-of-buddha{:words (first v)
                              :audio-url (->audio-url audio-url)
                              :translations [[lang translation]]}
     cite-block)))

(deftype BuddhaIngester []
  txt/Ingester

  (attr-for [_this_ kw]
    (types/namespace-kw :looped-words-of-buddha kw))

  (entry-attr [_this_]
    :looped-words-of-buddha/words)

  (human-name [_this_]
    "Words of Buddha")

  (parse [_this_ txt lang]
    (let [marker (get {"en" "Listen"
                       "es" "Escuchar"
                       "fr" "Ecouter " ;; NOTE: yes, it contains a space
                       "it" "Ascolta"
                       "pt" "Ouça"
                       "sr" "Slušaj"
                       "zh" "Listen"} lang)
          m (str marker ": ")]
      (->> (txt/split-file txt)
           (map strings/trim!)
           (map #(shred m %))
           (map #(repair m %))
           (map #(shred-blocks lang %)))))

  (find-existing [_this_ words]
    (first (db/q :looped-words-of-buddha/words (:looped-words-of-buddha/words words))))

  (db-insert* [_this_ words]
    (db/save! (merge #:looped-words-of-buddha
                     {:bookmarkable true
                      :shareable true
                      :original-words (:looped-words-of-buddha/words words)
                      :original-url (URI. "")}
                     words)))

  (citations [_this_ new]
    (if (= "en" (-> new :looped-words-of-buddha/translations first first))
      (select-keys new [:looped-words-of-buddha/citation
                        :looped-words-of-buddha/citation-url
                        :looped-words-of-buddha/store-title
                        :looped-words-of-buddha/store-url])
      {}))

  (download-attachments! [_this_ e]
    ;; TODO: actually download attachments
    (assoc e :looped-words-of-buddha/audio-attm-id (digest/null-uuid))))

(defn ingest [f lang]
  (txt/ingest (->BuddhaIngester) f lang))
