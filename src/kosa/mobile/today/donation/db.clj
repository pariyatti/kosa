(ns kosa.mobile.today.donation.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.record.nested :as nested]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support.debugging :refer :all]))

(defn list []
  (map expand-all (record/list :donation)))

(defn find-all [attr param]
  (query/find-all :donation attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :donation)
      (nested/collapse-one :donation/image-attachment)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (expand-all (record/get id)))
