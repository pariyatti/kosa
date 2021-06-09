(ns kosa.mobile.today.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.support.debugging :refer :all]))

(defn list []
  (record/list :pali-word))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :pali-word/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (record/query find-query param)))

(defn save! [e]
  (-> e
      (assoc :kuti/type :pali-word)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (record/get id))
