(ns kosa.mobile.today.looped-words-of-buddha.txt
  (:require [clojure.set]
            [clojure.string :as str]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped-words-of-buddha.db :as db]
            [kosa.mobile.today.looped.txt :as txt]
            [kuti.support.strings :as strings]
            [kuti.support.types :as types]
            [kuti.storage :as storage]
            [kuti.storage.open-uri :as open-uri])
  (:import [java.net URI]))

(def dwob-markers {"eng" "Listen"
                   "spa" "Escuchar"
                   "fra" "Ecouter " ;; NOTE: yes, it contains a space
                   "ita" "Ascolta"
                   "por" "Ouça"
                   "srp" "Slušaj"
                   "zho-hant" "Listen"})

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
    #:looped-words-of-buddha{:citepali     (get cite 0)
                             :citepali-url (URI. (get cite 1))
                             :citebook     (or (get cite 2) "")
                             :citebook-url (URI. (or (get cite 3) ""))}))

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
                              :original-audio-url (->audio-url audio-url)
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
    (let [marker (get dwob-markers lang)
          m (str marker ": ")]
      (->> (txt/split-file txt)
           (map strings/trim!)
           (map #(shred m %))
           (map #(repair m %))
           (map #(shred-blocks lang %)))))

  (find-existing [_ words]
    (first (db/find-all :looped-words-of-buddha/words (:looped-words-of-buddha/words words))))

  (db-insert* [_ words]
    (db/save! (merge #:looped-words-of-buddha
                     {:original-words (:looped-words-of-buddha/words words)
                      :original-url (URI. "")}
                     words)))

  (citations [_ new]
    (if (= "eng" (-> new :looped-words-of-buddha/translations first first))
      (select-keys new [:looped-words-of-buddha/citepali
                        :looped-words-of-buddha/citepali-url
                        :looped-words-of-buddha/citebook
                        :looped-words-of-buddha/citebook-url])
      {}))

  (download-attachments! [_ lang e]
    (if (= "eng" lang)
      (let [file (open-uri/download-uri! (:looped-words-of-buddha/original-audio-url e))]
        (storage/attach! e :looped-words-of-buddha/audio-attachment file))
      e))

  (reconcile [_ lang txt-file-entry-count]
    (let [db-count (count (db/list))
          diff {:lang lang
                :db-count db-count
                :txt-count txt-file-entry-count}]
      (when (not= txt-file-entry-count db-count)
        (throw (ex-info (str "TXT file entry count did not match!\n\n"
                             diff)
                        diff))))))

(defn truncate! []
  (db/truncate!))

(defn ingest [f lang]
  (txt/ingest (BuddhaIngester.) f lang))

(defn validate []
  (doseq [card (db/list)]
    (let [translations (:looped-words-of-buddha/translations card)
          diff {:actual-langs translations
                :expected-langs (keys dwob-markers)}]
      (when (not= (count translations)
                  (count dwob-markers))
        (throw (ex-info (str "TXT translation count did not match!\n\n"
                             diff)
                        diff))))))
