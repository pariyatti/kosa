(ns kosa.mobile.today.stacked-inspiration.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.storage :as storage]
            [kuti.support.time :as time]
            [kuti.record.nested :as nested]))

(defn rehydrate [card]
  (as-> (nested/expand-all card :stacked-inspiration/image-attachment) c
      (assoc-in c
       [:stacked-inspiration/image-attachment :url]
       (storage/url (:stacked-inspiration/image-attachment c)))))

(defn list []
  (let [list-query '{:find     [e updated-at]
                     :where    [[e :type :stacked-inspiration]
                                [e :updated-at updated-at]]
                     :order-by [[updated-at :desc]]}]
    (map rehydrate (record/query list-query))))

(defn save! [e]
  (let [doc (assoc e :type :stacked-inspiration)]
    (-> doc
        (nested/collapse-one :stacked-inspiration/image-attachment)
        (record/save!))))

(defn get [id]
  (rehydrate (record/get id)))
