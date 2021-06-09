(ns kosa.mobile.today.words-of-buddha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.storage :as storage]
            [kuti.support.debugging :refer :all]))

(defn rehydrate [card]
  (as-> (nested/expand-all card :looped-words-of-buddha/audio-attachment) c
    ;; TODO: this behaviour really belongs in kuti.storage
    (assoc-in c
              [:looped-words-of-buddha/audio-attachment :attm/url]
              (storage/url (:looped-words-of-buddha/audio-attachment c)))))

(defn list []
  (map rehydrate (record/list :words-of-buddha)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :words-of-buddha/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (map rehydrate (record/query find-query param))))

(defn save! [e]
  (-> e
      (assoc :kuti/type :words-of-buddha)
      (nested/collapse-one :words-of-buddha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (record/get id))
