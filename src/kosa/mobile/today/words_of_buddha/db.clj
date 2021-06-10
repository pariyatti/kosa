(ns kosa.mobile.today.words-of-buddha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.record.nested :as nested]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support.debugging :refer :all]))

(defn list []
  (map expand-all (record/list :words-of-buddha)))

(defn find-all [attr param]
  (query/find-all :words-of-buddha attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :words-of-buddha)
      (nested/collapse-one :words-of-buddha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (expand-all (record/get id)))
