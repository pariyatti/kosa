(ns kosa.mobile.today.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.support.debugging :refer :all]))

(defn list []
  (record/list :pali-word))

(defn find-all [attr param]
  (query/find-all :pali-word attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :pali-word)
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (record/get id))
