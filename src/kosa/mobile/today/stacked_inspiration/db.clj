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
  (map rehydrate (record/list :stacked-inspiration)))

(defn save! [e]
  (-> e
      (assoc :type :stacked-inspiration)
      (nested/collapse-one :stacked-inspiration/image-attachment)
      record/timestamp
      record/publish
      record/save!))

(defn get [id]
  (rehydrate (record/get id)))
