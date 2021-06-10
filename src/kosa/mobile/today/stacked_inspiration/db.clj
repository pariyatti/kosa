(ns kosa.mobile.today.stacked-inspiration.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.record.nested :as nested]))

(defn list []
  (map expand-all (record/list :stacked-inspiration)))

(defn find-all [attr param]
  (query/find-all :stacked-inspiration attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :stacked-inspiration)
      (nested/collapse-one :stacked-inspiration/image-attachment)
      record/timestamp
      record/publish
      record/save!))

(defn get [id]
  (expand-all (record/get id)))
