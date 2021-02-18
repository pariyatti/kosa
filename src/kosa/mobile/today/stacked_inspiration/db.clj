(ns kosa.mobile.today.stacked-inspiration.db
  (:refer-clojure :exclude [list get])
  (:require [kutis.record]
            [kutis.storage :as storage]
            [kutis.support.time :as time]
            [kutis.record.nested :as nested]))

(def fields #{:card-type
              :modified-at
              :published-at
              :bookmarkable
              :shareable
              :text
              :image-attachment-id})

(defn rehydrate [image]
  (as-> (nested/expand-all image :image-attachment) img
      (assoc-in img
       [:image-attachment :url]
       (storage/url (:image-attachment img)))))

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :card-type "stacked_inspiration"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}]
    (map rehydrate (kutis.record/query list-query))))

(defn put [e]
  ;; TODO: we need a low-level home for applying `:modified-at` to all entities
  (let [doc (assoc e
                   :modified-at (time/now)
                   :card-type "stacked_inspiration")]
    (-> doc
        (nested/collapse-one :image-attachment)
        (kutis.record/put fields))))

(defn get [id]
  (rehydrate (kutis.record/get id)))
