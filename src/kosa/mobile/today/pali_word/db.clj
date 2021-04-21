(ns kosa.mobile.today.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]))

(defn list []
  (record/list :pali-word))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :in       '[original-pali]
                    :where    [['e attr 'original-pali]
                               '[e :pali-word/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]}]
    (record/query find-query param)))

;; TODO: it's likely this just belongs in `record` directly?
(defn publish [e]
  (if-let [pub (:pali-word/published-at e)]
    (record/publish e pub)
    (record/publish e)))

(defn save! [e]
  (-> e
      (assoc :kuti/type :pali-word)
      record/timestamp
      publish
      (record/save!)))

(defn get [id]
  (record/get id))
