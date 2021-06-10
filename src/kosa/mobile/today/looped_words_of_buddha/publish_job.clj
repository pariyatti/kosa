(ns kosa.mobile.today.looped-words-of-buddha.publish-job
  (:require [kosa.mobile.today.looped.publish-job :as job]
            [kosa.mobile.today.looped-words-of-buddha.db :as loop-db]
            [kosa.mobile.today.words-of-buddha.db :as buddha-db]))

;; Looping can be compared against:
;; https://rss.pariyatti.org/dwob_english.rss

(deftype BuddhaPublisher []
  job/Publisher
  (type [_] :words-of-buddha)
  (main-key [_] :words-of-buddha/words)
  (published-at-key [_] :words-of-buddha/published-at)
  (looped-list [_] (loop-db/list))
  (looped-find [_ idx] (loop-db/q :looped-words-of-buddha/index idx))
  (entity-find [_ card] (buddha-db/q :words-of-buddha/words
                                     (:words-of-buddha/words card)))
  (save! [_ card] (buddha-db/save! card)))

(defn run-job! [t]
  (job/run-job! (BuddhaPublisher.) t))
