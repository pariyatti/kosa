(ns kosa.mobile.today.looped-words-of-buddha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.record.query :as query]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support :refer [assoc-unless]]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped.db :refer [next-index]]))

(defn list []
  (map expand-all (record/list :looped-words-of-buddha)))

(defn truncate! []
  (record/truncate! :looped-words-of-buddha))

(defn find-all [attr param]
  (query/find-all :looped-words-of-buddha attr param))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-words-of-buddha)
      (assoc-unless :looped-words-of-buddha/index (next-index :looped-words-of-buddha))
      (nested/collapse-one :looped-words-of-buddha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))
