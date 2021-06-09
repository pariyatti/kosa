(ns kosa.mobile.today.looped-doha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.storage :as storage]
            [kuti.support :refer [assoc-unless]]
            [kuti.support.debugging :refer :all]))

;; TODO: pali word + words of buddha must be refactored
;;       before work starts on this. -sd

(defn rehydrate [card]
  (as-> (nested/expand-all card :looped-doha/audio-attachment) c
    ;; TODO: this behaviour really belongs in kuti.storage
    (assoc-in c
              [:looped-doha/audio-attachment :attm/url]
              (storage/url (:looped-doha/audio-attachment c)))))

(defn list []
  (map rehydrate (record/list :looped-doha)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :where    [['e attr 'v]
                               '[e :looped-doha/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]
                    :in       '[v]}]
    (map rehydrate (record/query find-query param))))

(defn next-index []
  (let [find-query '{:find     [(max ?idx)]
                     :where    [[e :looped-doha/index ?idx]]}
        result (-> (record/q find-query) first first)]
    (if result
      (+ 1 result)
      0)))

;; TODO: it's likely this just belongs in `record` directly?
(defn publish [e]
  (if-let [pub (:looped-doha/published-at e)]
    (record/publish e pub)
    (record/publish e)))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-doha)
      (assoc-unless :looped-doha/index (next-index))
      (nested/collapse-one :looped-doha/audio-attachment)
      record/timestamp
      publish
      (record/save!)))
