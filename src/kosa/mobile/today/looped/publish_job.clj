(ns kosa.mobile.today.looped.publish-job
  (:refer-clojure :exclude [type])
  (:require [clojure.tools.logging :as log]
            [kuti.support.types :as types]
            [kuti.support.time :as time]
            [kuti.record :as record]
            [tick.alpha.api :as tick]
            [clojure.string :as clojure.string]))

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

(defprotocol Publisher
  (type [this])
  (offset [this])
  (main-key [this])
  (published-at-key [this])
  (looped-list [this])
  (looped-find [this idx])
  (entity-find [this card])
  (save! [this card]))

(defn publish-time [pub now]
  (-> (time/extract-date now)
      (time/at (offset pub))
      (time/to-no-timezone)
      (time/pst-to-utc)))

(defn publish-nth [pub cc]
  (let [pub-time (publish-time pub (time/now))
        idx (which-card pub-time cc)
        card (-> (looped-find pub idx)
                 first
                 (types/dup (type pub)))
        existing (entity-find pub card)
        published-at (published-at-key pub)
        save-fn (partial save! pub)]
    (log/info (format "#### Today's %s is: %s" (type pub) (get card (main-key pub))))
    (if (or (empty? existing)
            (< 2 (time/days-between (-> existing first published-at)
                                    pub-time)))
      (-> card
          (record/publish-at pub-time)
          save-fn)
      (log/info (format "#### Ignoring. '%s' already exists within a 2-day window." (get card (main-key pub)))))))

;; TODO: there is an extremely annoying circular reference between this fn
;;       and the other looped_* `publish_job.clj` files. This makes the
;;       `publish_job_test.clj` tests annoying to run because you must
;;       compile the job, then this file, then the job again. If you don't,
;;       you will see a 'no method :type defined for Protocol' error. FIXME
(defn run-job! [pub _]
  (log/info (format "#### Running looped %s publish job" (type pub)))
  (let [cc (count (looped-list pub))]
    (if (< 0 cc)
      (publish-nth pub cc)
      (log/info (format "#### Zero looped %s found." (type pub))))))
