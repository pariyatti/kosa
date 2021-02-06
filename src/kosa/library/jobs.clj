(ns kosa.library.jobs
  (:require [clojure.tools.logging :as log]
            [chime.core :as chime]
            [kutis.mailer :as mailer]
            [kosa.mobile.today.pali-word.rss-job :as pali-word]
            [mount.core :as mount :refer [defstate]])
  (:import [java.time Duration Instant]))

(def rss-job)

(defn start-rss-job! []
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofSeconds 5))
                      rest)

                  (fn [time]
                    (println "Chiming at" time)
                    (println (pali-word/run-job!)))

                  {:error-handler (fn [e]
                                    (log/error e "RSS job failed.")
                                    (mailer/send-alert (format "RSS job failed:\n\n%s" e))
                                    :continue-schedule)}))

(defn stop-rss-job! []
  (.close rss-job))

(defstate rss-job
  :start (start-rss-job!)
  :stop (stop-rss-job!))

;; (mount/only #{#'rss-job})
;; (mount/start #{#'rss-job})
;; (mount/stop #{#'rss-job})
