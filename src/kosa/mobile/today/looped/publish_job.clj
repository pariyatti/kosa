(ns kosa.mobile.today.looped.publish-job
  (:refer-clojure :exclude [type])
  (:require [clojure.tools.logging :as log]
            [kuti.support.types :as types]
            [kuti.support.time :as time]
            [kuti.record :as record]))

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
  (main-key [this])
  (published-at-key [this])
  (looped-list [this])
  (looped-find [this idx])
  (entity-find [this card])
  (save! [this card]))

(defn publish-nth [pub cc]
  (let [idx (which-card (time/now) cc)
        card (-> (looped-find pub idx)
                 first
                 (types/dup (type pub)))
        existing (entity-find pub card)
        published-at (published-at-key pub)
        save-fn (partial save! pub)]
    (log/info (format "#### Today's %s is: %s" (type pub) (get card (main-key pub))))
    (if (or (empty? existing)
            (< 0 (time/days-between (-> existing first published-at)
                                    (time/now))))
      (-> card
          record/republish
          save-fn)
      (log/info (format "#### Ignoring. '%s' already exists." (get card (main-key pub)))))))

(defn run-job! [pub _]
  (log/info (format "#### Running looped %s publish job" (type pub)))
  (let [cc (count (looped-list pub))]
    (if (< 0 cc)
      (publish-nth pub cc)
      (log/info (format "#### Zero looped %s found." (type pub))))))
