(ns kosa.mobile.today.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kutis.record]))

(def fields #{:type
              :card-type
              :modified-at
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
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :card-type "pali_word"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}]
    (kutis.record/query list-query)))

(defn q [attr param]
  (let [find-query {:find     '[e modified-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :modified-at modified-at]]
                    :order-by '[[modified-at :desc]]}]
    (kutis.record/query find-query param)))

(defn put [e]
  ;; TODO: we need a low-level home for applying `:modified-at` to all entities
  (kutis.record/put (assoc e :modified-at (java.util.Date.)) fields))

(defn get [id]
  (kutis.record/get id))
