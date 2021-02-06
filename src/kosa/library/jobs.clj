(ns kosa.library.jobs
  (:require [clojure.tools.logging :as log]
            [chime.core :as chime]
            [kutis.mailer :as mailer]
            [mount.core :as mount :refer [defstate]])
  (:import [java.time Duration Instant]))

(def jobs (atom {}))
(def rss-job)

(defn run-jobs [time]
  (log/info (str "Chiming at " time))
  (doseq [[job-name job-fn] @jobs]
    (log/info (str "Running job: " job-name))
    (job-fn time)))

(defn start-rss-job! []
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofSeconds 5))
                      rest)

                  run-jobs

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

(defn add-job [job-name job]
  (swap! jobs assoc job-name job))

(defn remove-job [job-name]
  (swap! jobs dissoc job-name))
