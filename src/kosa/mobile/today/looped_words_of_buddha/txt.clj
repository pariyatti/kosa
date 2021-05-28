(ns kosa.mobile.today.looped-words-of-buddha.txt
  (:require [clojure.set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped-words-of-buddha.db :as db]
            [kosa.mobile.today.looped.txt :as txt]
            [kuti.support.strings :as strings]
            [kuti.support.types :as types]
            [kuti.storage :as storage]
            [kuti.storage.open-uri :as open-uri])
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
      strings/trim!
      (URI.)))

(defn shred-cite-block [s]
  (let [cite-dirty (str/split s (re-pattern "\n"))
        cite (mapv strings/trim! cite-dirty)]
    #:looped-words-of-buddha{:citation     (get cite 0)
                             :citation-url (URI. (get cite 1))
                             :store-title  (or (get cite 2) "")
                             :store-url    (URI. (or (get cite 3) ""))}))

(defn shred-blocks [lang v]
  (let [all-blocks (str/split (second v) (re-pattern "\n\\s*\n"))
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

  (attr-for [_ kw]
    (types/namespace-kw :looped-words-of-buddha kw))

  (entry-attr [_]
    :looped-words-of-buddha/words)

  (human-name [_]
    "Words of Buddha")

  (parse [_ txt lang]
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

  (find-existing [_ words]
    (first (db/q :looped-words-of-buddha/words (:looped-words-of-buddha/words words))))

  (db-insert* [_ words]
    (db/save! (merge #:looped-words-of-buddha
                     {:bookmarkable true
                      :shareable true
                      :original-words (:looped-words-of-buddha/words words)
                      :original-url (URI. "")}
                     words)))

  (citations [_ new]
    (if (= "en" (-> new :looped-words-of-buddha/translations first first))
      (select-keys new [:looped-words-of-buddha/citation
                        :looped-words-of-buddha/citation-url
                        :looped-words-of-buddha/store-title
                        :looped-words-of-buddha/store-url])
      {}))

  (download-attachments! [_ e]
    (let [file (open-uri/download-uri! (:looped-words-of-buddha/audio-url e))]
      (storage/attach! e :looped-words-of-buddha/audio-attachment file))))

(defn ingest [f lang]
  (txt/ingest (BuddhaIngester.) f lang))
