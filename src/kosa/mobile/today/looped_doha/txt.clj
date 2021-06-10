(ns kosa.mobile.today.looped-doha.txt
  (:require [kosa.mobile.today.looped.txt :as txt]
            [kosa.mobile.today.looped-doha.db :as db]
            [kuti.support.types :as types]
            [kuti.storage :as storage]
            [kuti.storage.open-uri :as open-uri])
  (:import [java.net URI]))

(deftype DohaIngester []
  txt/Ingester

  (attr-for [_ kw]
    (types/namespace-kw :looped-doha kw))

  (entry-attr [_]
    :looped-doha/doha)

  (human-name [_]
    "Daily Doha")

  (parse [_ txt lang]
    (let [marker (get {"en" "Listen"
                       "es" "Escuchar"
                       "fr" "Ecouter " ;; NOTE: yes, it contains a space
                       "it" "Ascolta"
                       "pt" "Ouça"
                       "sr" "Slušaj"
                       "zh" "Listen"} lang)
          m (str marker ": ")]
      #_(->> (txt/split-file txt)
           (map strings/trim!)
           (map #(shred m %))
           (map #(repair m %))
           (map #(shred-blocks lang %)))))

  (find-existing [_ doha]
    (first (db/find-all :looped-doha/doha (:looped-doha/doha doha))))

  (db-insert* [_ doha]
    (db/save! (merge #:looped-doha
                     {:original-doha (:looped-doha/doha doha)
                      :original-url (URI. "")}
                     doha)))

  (citations [_ new]
    (if (= "en" (-> new :looped-doha/translations first first))
      (select-keys new [:looped-doha/citepali
                        :looped-doha/citepali-url
                        :looped-doha/citebook
                        :looped-doha/citebook-url])
      {}))

  (download-attachments! [_ lang e]
    (if (= "en" lang)
      (let [file (open-uri/download-uri! (:looped-doha/audio-url e))]
        (storage/attach! e :looped-doha/audio-attachment file))
      e)))

(defn ingest [f lang]
  (txt/ingest (DohaIngester.) f lang))
