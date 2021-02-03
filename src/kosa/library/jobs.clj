(ns kosa.library.jobs
  (:require [chime.core :as chime]
            [mount.core :as mount :refer [defstate]]
            [kosa.mobile.today.pali-word.rss-parser :as pali-word])
  (:import [java.time Instant Duration]))

(def rss-job)

(defn start-rss-job! []
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofSeconds 5))
                      rest)

                  (fn [time]
                    (println "Chiming at" time)
                    (println (pali-word/poll)))))

(defn stop-rss-job! []
  (.close rss-job))

(defstate rss-job
  :start (start-rss-job!)
  :stop (stop-rss-job!))
