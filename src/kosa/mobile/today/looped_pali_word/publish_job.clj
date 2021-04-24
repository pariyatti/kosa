(ns kosa.mobile.today.looped-pali-word.publish-job
  (:require [clojure.tools.logging :as log]
            [kosa.mobile.today.looped-pali-word.db :as loop-db]
            [kosa.mobile.today.pali-word.db :as pali-db]
            [kuti.support.types :as types]))

(defn run-job! [_]
  (log/info "#### Running looped pali word publish job")
  (let [word (-> (loop-db/list) first (types/dup :pali-word))]
    (pali-db/save! word)))

;; (def looped-card-count 220)
;; (def days-since-epoch (t/days (t/between (t/epoch) (t/now))))
;; (def days-since-perl (- days-since-epoch 12902))
;; (def todays-word (mod days-since-perl looped-card-count))
