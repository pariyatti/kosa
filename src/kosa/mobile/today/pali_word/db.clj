(ns kosa.mobile.today.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]))

(defn list []
  (let [list-query '{:find     [e updated-at]
                     :where    [[e :type :pali-word]
                                [e :updated-at updated-at]]
                     :order-by [[updated-at :desc]]}]
    (record/query list-query)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (record/query find-query param)))

(defn save! [e]
  (-> e
      (assoc :type :pali-word)
      (record/save!)))

(defn get [id]
  (record/get id))
