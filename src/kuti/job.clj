(ns kuti.job
  (:require
   [mount.core :as mount :refer [defstate]]
   [clojure.tools.logging :as log]
   [kuti.support.time :as time]
   [kuti.mailer :as mailer]
   [kosa.config :as config]
   [chime.core :as chime]))

(defn default-error-handler [job-name]
  (fn [e]
    (log/error e (format "Kuti.job '%s' failed." job-name))
    (mailer/send-alert (format "Kuti.job '%s' failed:\n\n%s" job-name e))
    :continue-schedule))

(defn load-jobs [jobs]
  (for [{:keys [job-name offset-seconds period-seconds job-fn enabled]} jobs
        :when enabled]
    (let [job (chime/chime-at
               (time/schedule offset-seconds period-seconds)

               (resolve job-fn)

               {:error-handler (default-error-handler job-name)})]
      [job-name job])))

(defn start-jobs! []
  (->> (:jobs config/config)
       load-jobs
       (into {})))

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
