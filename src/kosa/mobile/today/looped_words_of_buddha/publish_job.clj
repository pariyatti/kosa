(ns kosa.mobile.today.looped-words-of-buddha.publish-job
  (:require [clojure.tools.logging :as log]
            [kosa.mobile.today.looped-words-of-buddha.db :as loop-db]
            [kosa.mobile.today.words-of-buddha.db :as buddha-db]
            [kuti.support.types :as types]
            [kuti.support.time :as time]
            [kuti.record :as record]))

;; Looping can be compared against:
;; https://rss.pariyatti.org/dwob_english.rss

;; (def looped-card-count 220)
;; (def days-since-epoch (t/days (t/between (t/epoch) (t/now))))
;; (def days-since-perl (- days-since-epoch 12902))
;; (def todays-word (mod days-since-perl looped-card-count))

(defn which-card [today card-count]
  ;; "perl epoch":
  ;; (t/>> (t/epoch)
  ;;       (t/new-duration 12902 :days))
  ;; => #time/instant "2005-04-29T00:00:00Z"
  (mod (time/days-between "2005-04-29T00:00:00Z" today)
       card-count))

(defn publish-nth [n]
  (let [idx (which-card (time/now) n)
        word (-> (loop-db/q :looped-words-of-buddha/index idx)
                 first
                 (types/dup :words-of-buddha))
        existing (buddha-db/q :words-of-buddha/words (:words-of-buddha/words word))]
    (log/info (str "#### Today's words-of-buddha is: " (:words-of-buddha/words word)))
    (if (or (empty? existing)
            (< 0 (time/days-between (-> existing first :words-of-buddha/published-at)
                                    (time/now))))
      (-> word
          record/publish
          buddha-db/save!)
      (log/info (format "#### Ignoring. '%s' already exists." (:words-of-buddha/words word))))))

(defn run-job! [_]
  (log/info "#### Running looped words-of-buddha publish job")
  (let [n (count (loop-db/list))]
    (if (< 0 n)
      (publish-nth n)
      (log/info "#### Zero looped words-of-buddha found."))))
