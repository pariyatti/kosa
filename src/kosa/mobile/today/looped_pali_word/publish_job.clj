(ns kosa.mobile.today.looped-pali-word.publish-job
  (:require [clojure.tools.logging :as log]
            [kosa.mobile.today.looped-pali-word.db :as loop-db]
            [kosa.mobile.today.pali-word.db :as pali-db]
            [kuti.support.types :as types]
            [tick.alpha.api :as t]
            [kuti.support.time :as time]))

;; (def looped-card-count 220)
;; (def days-since-epoch (t/days (t/between (t/epoch) (t/now))))
;; (def days-since-perl (- days-since-epoch 12902))
;; (def todays-word (mod days-since-perl looped-card-count))

(def perl-epoch-offset 12902)

(defn which-card [today card-count]
  ;; TODO: consider moving a generic version of this into kuti.support.time ?
  ;; TODO: yes. use `days-between` and an actual date. -sd
  (-> (t/days (t/between (t/epoch) today))
      (- perl-epoch-offset)
      (mod card-count)))

(defn run-job! [_]
  (log/info "#### Running looped pali word publish job")
  (let [idx (which-card (time/now) (count (loop-db/list)))
        word (-> (loop-db/q :looped-pali-word/index idx)
                 first
                 (types/dup :pali-word))]
    (pali-db/save! word)))

;; "perl epoch":
;; (t/>> (t/epoch)
;;       (t/new-duration 12902 :days))
;; => #time/instant "2005-04-29T00:00:00Z"
