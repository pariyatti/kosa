(ns kosa.mobile.today.looped-pali-word.publish-job
  (:refer-clojure :exclude [type])
  (:require [kosa.mobile.today.looped.publish-job :as job]
            [kosa.mobile.today.looped-pali-word.db :as loop-db]
            [kosa.mobile.today.pali-word.db :as pali-db]))

;; Looping can be compared against:
;; https://download.pariyatti.org/pwad/pali_words.xml

(deftype PaliPublisher []
  job/Publisher
  (type [_] :pali-word)
  (main-key [_] :pali-word/pali)
  (published-at-key [_] :pali-word/published-at)
  (looped-list [_] (loop-db/list))
  (looped-find [_ idx] (loop-db/find-all :looped-pali-word/index idx))
  (entity-find [_ card] (pali-db/find-all :pali-word/pali
                                   (:pali-word/pali card)))
  (save! [_ card] (pali-db/save! card)))

(defn run-job! [t]
  (job/run-job! (PaliPublisher.) t))
