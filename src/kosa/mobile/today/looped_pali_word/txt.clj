(ns kosa.mobile.today.looped-pali-word.txt
  (:require [clojure.set]
            [clojure.string :as str]
            [kuti.support.debugging :refer :all]
            [kuti.support.types :as types]
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

(deftype PaliIngester []
  txt/Ingester

  (attr-for [_ kw]
    (types/namespace-kw :looped-pali-word kw))

  (entry-attr [_]
    :looped-pali-word/pali)

  (human-name [_]
    "Pali Word")

  (parse [_ txt lang]
    (->> (txt/split-file txt)
         (map str/trim)
         (map shred)
         (map (partial ->doc lang))))

  (find-existing [_ pali-word]
    (first (db/find-all :looped-pali-word/pali (:looped-pali-word/pali pali-word))))

  (db-insert* [_ pali-word]
    (db/save! (merge #:looped-pali-word
                     {:original-pali (:looped-pali-word/pali pali-word)
                      :original-url (URI. "")}
                     pali-word)))

  (citations [_ _entity]
    {})

  (download-attachments! [_ _lang e]
    e))

(defn ingest [f lang]
  (txt/ingest (PaliIngester.) f lang))

(defn validate []
  true)
