(ns kosa.mobile.today.looped-doha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.record.nested :as nested]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support :refer [assoc-unless]]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped.db :refer [next-index]]))

;; TODO: pali word + words of buddha must be refactored
;;       before work starts on this. -sd

(defn list []
  (map expand-all (record/list :looped-doha)))

(defn find-all [attr param]
  (query/find-all :looped-doha attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-doha)
      (assoc-unless :looped-doha/index (next-index :looped-doha))
      (nested/collapse-one :looped-doha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))
