(ns kosa.mobile.today.stacked-inspiration.db
  (:refer-clojure :exclude [list get])
  (:require [kutis.record]
            [kutis.storage :as storage]
            [kutis.support.time :as time]
            [kutis.record.nested :as nested]))

(def fields #{:card-type
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
  (let [list-query '{:find     [e updated-at]
                     :where    [[e :card-type "stacked_inspiration"]
                                [e :updated-at updated-at]]
                     :order-by [[updated-at :desc]]}]
    (map rehydrate (kutis.record/query list-query))))

(defn put [e]
  (let [doc (assoc e :card-type "stacked_inspiration")]
    (-> doc
        (nested/collapse-one :image-attachment)
        (kutis.record/put fields))))

(defn get [id]
  (rehydrate (kutis.record/get id)))
