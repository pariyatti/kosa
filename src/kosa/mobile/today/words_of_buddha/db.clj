(ns kosa.mobile.today.words-of-buddha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.storage :as storage]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support.debugging :refer :all]))

(defn list []
  (map expand-all (record/list :words-of-buddha)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :words-of-buddha/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (map expand-all (record/query find-query param))))

(defn save! [e]
  (-> e
      (assoc :kuti/type :words-of-buddha)
      (nested/collapse-one :words-of-buddha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (expand-all (record/get id)))
