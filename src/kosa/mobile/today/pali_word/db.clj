(ns kosa.mobile.today.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kutis.record]
            [kutis.support.time :as time]))

(def fields #{:type
              :card-type
              :published-at
              :original-pali ;; from *.pariyatti.org - a long string
              :original-url  ;; from *.pariyatti.org
              :bookmarkable
              :shareable
              :pali
              :translations
              ;; TODO: remove or make these work-
              :header :id :audio})

(defn list []
  (let [list-query '{:find     [e updated-at]
                     :where    [[e :card-type "pali_word"]
                                [e :updated-at updated-at]]
                     :order-by [[updated-at :desc]]}]
    (kutis.record/query list-query)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (kutis.record/query find-query param)))

(defn put [e]
  (kutis.record/put e fields))

(defn get [id]
  (kutis.record/get id))
