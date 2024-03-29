(ns kuti.job
  (:require
   [mount.core :as mount :refer [defstate]]
   [clojure.tools.logging :as log]
   [kuti.support.time :as time]
   [kuti.mailer :as mailer]
   [kosa.config :as config]
   [chime.core :as chime])
  (:import
   [java.time Instant]))

(defn default-error-handler [job-name]
  (fn [e]
    (log/error e (format "Kuti.job '%s' failed." job-name))
    (mailer/send-alert (format "Kuti.job '%s' failed:\n\n%s" job-name e))
    :continue-schedule))

;; TODO: Should we consider cron-style recurring events instead, so they are more
;;       consistent, regardless of when the server was started? -sd
;;       https://github.com/ivarref/recurring-cup
(defn load-job [{:keys [job-name offset-seconds period-seconds job-fn enabled]}]
  (when-not enabled
    ;; please excuse the Java-style guard
    (log/info (str "#### Job disabled: " job-name)))
  (when enabled
    (if-let [fn (resolve job-fn)]
      (do (log/info (format "#### Installing job: %s" job-name))
          (let [job (chime/chime-at
                     (lazy-cat
                      [(.plusSeconds (Instant/now) offset-seconds)]
                      (time/schedule 0 period-seconds))
                     fn
                     {:error-handler (default-error-handler job-name)})]
            [job-name job]))
      (throw (Exception. (str "Job fn resolved to nil: " job-name))))))

(defn load-jobs [jobs]
  (for [job jobs]
    (load-job job)))

(defn start-jobs! []
  (let [jobs (:jobs config/config)]
    (log/info (str "#### Installing jobs: \n" jobs))
    (->> jobs
         load-jobs
         (into {}))))

(defn stop-jobs! [job-map]
  (doseq [[job-name job] job-map]
    (log/info (format "Stopping job '%s'." job-name))
    (.close job)))

(defstate jobs
  :start (start-jobs!)
  :stop (stop-jobs! jobs))

;; (mount/only #{#'jobs})
;; (mount/start #{#'jobs})
;; (mount/stop #{#'jobs})
