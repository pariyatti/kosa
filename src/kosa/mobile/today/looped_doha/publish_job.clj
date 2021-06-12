(ns kosa.mobile.today.looped-doha.publish-job
  (:require [kosa.mobile.today.looped.publish-job :as job]
            [kosa.mobile.today.looped-doha.db :as loop-db]
            [kosa.mobile.today.doha.db :as doha-db]))

;; Looping can be compared against:
;; https://download.pariyatti.org/dohas/daily_dohas.xml

(deftype DohaPublisher []
  job/Publisher
  (type             [_]      :doha)
  (main-key         [_]      :doha/doha)
  (published-at-key [_]      :doha/published-at)
  (looped-list      [_]      (loop-db/list))
  (looped-find      [_ idx]  (loop-db/find-all :looped-doha/index idx))
  (entity-find      [_ card] (doha-db/find-all :doha/doha
                                               (:doha/doha card)))
  (save!            [_ card] (doha-db/save! card)))

(defn run-job! [t]
  (job/run-job! (DohaPublisher.) t))
