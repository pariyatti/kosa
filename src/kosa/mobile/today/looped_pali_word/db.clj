(ns kosa.mobile.today.looped-pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]))

(defn list []
  (record/list :looped-pali-word))

(defn next-index []
  (let [find-query '{:find     [(max ?idx)]
                     :where    [[e :looped-pali-word/index ?idx]]}
        result (-> (record/q find-query) first first)]
    (if result
      (+ 1 result)
      0)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :looped-pali-word/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (record/query find-query param)))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-pali-word)
      (assoc :looped-pali-word/index (next-index))
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (record/get id))
