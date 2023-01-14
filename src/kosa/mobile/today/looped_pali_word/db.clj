(ns kosa.mobile.today.looped-pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped.db :refer [next-index]]))

(defn list []
  (record/list :looped-pali-word))

(defn truncate! []
  (record/truncate! :looped-pali-word))

(defn find-all [attr param]
  (query/find-all :looped-pali-word attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-pali-word)
      (assoc :looped-pali-word/index (next-index :looped-pali-word))
      record/timestamp
      record/publish
      (record/save!)))

(defn get [id]
  (record/get id))
