(ns kosa.mobile.today.doha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.record.nested :as nested]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support.debugging :refer :all]))

(defn list []
  (map expand-all (record/list :doha)))

(defn find-all [attr param]
  (query/find-all :doha attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :doha)
      (nested/collapse-one :doha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (expand-all (record/get id)))
