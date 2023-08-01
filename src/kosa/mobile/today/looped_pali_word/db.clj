(ns kosa.mobile.today.looped-pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [clojure.tools.logging :as log]
            [kuti.record :as record]
            [kuti.record.query :as query]
            [kuti.support :refer [assoc-unless]]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped.db :refer [next-index]]))

(defn list []
  (record/list :looped-pali-word))

(defn truncate! []
  (record/truncate! :looped-pali-word))

(defn find-all [attr param]
  (query/find-all :looped-pali-word attr param))

(defn save! [e]
  (let [idx (next-index :looped-pali-word)]
    (log/info (format "Saving Looped Pali Word '%s' with index: %s"
                      (:looped-pali-word/pali e)
                      (or (:looped-pali-word/index e) idx)))
    (-> e
        (assoc :kuti/type :looped-pali-word)
        (assoc-unless :looped-pali-word/index idx)
        record/timestamp
        record/publish
        (record/save!))))

(defn get [id]
  (record/get id))
