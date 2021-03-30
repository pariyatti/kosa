(ns kutis.support.time
  (:refer-clojure :exclude [time])
  (:require [tick.alpha.api :as t]
            [chime.core :as chime]
            [clojure.string :as clojure.string])
  (:import [java.time LocalDate ZonedDateTime ZoneId]
           [java.time.chrono Era IsoEra IsoChronology]))

(def ^:dynamic clock (t/atom))

(defn now []
  @clock)

(defn instant
  "This fn is a little silly, but using `kutis.support.time`
   exclusively ensures we don't accidentally consume the more
   powerful features from `tick`."
  [time]
  (t/instant time))

(defn parse [s]
  (t/parse s))

(defn string
  "Force the `yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format."
  [time]
  (let [inst (t/instant time)
        s (str inst)]
    (if (= 24 (count s))
      s
      (clojure.string/replace s #"Z$" ".000Z"))))

(defn schedule [offset-seconds period-seconds]
  (-> (chime/periodic-seq (t/>> (now) (t/new-duration offset-seconds :seconds))
                          (t/new-duration period-seconds :seconds))
      rest))

;; dates and publishing:

(def CE (IsoEra/CE))
(def BCE (IsoEra/BCE))

(defn date
  "Get a date, modern or ancient, from its components. Prefer
   the signature with explicit `era` wherever possible."
  ([era year-of-era]
   (date era year-of-era 1 1))
  ([era year-of-era month day]
   (if (and (= BCE era)
            (< year-of-era 0))
     (throw (java.lang.IllegalArgumentException.
             (format "Negative year '%s' supplied for BCE date." year-of-era)))
     (.date (IsoChronology/INSTANCE) era year-of-era month day)))
  ([proleptic-year month day]
   (.date (IsoChronology/INSTANCE) proleptic-year month day)))

(def time t/new-time)

(defmulti date-time
  "Always use this public API to create :published-at date/times."
  (fn [d & args] (class d)))

(defmethod date-time java.time.LocalDate
  ([d]   (date-time d (time 0 0 0)))
  ([d t] (t/instant (ZonedDateTime/of d t (ZoneId/of "UTC")))))

;; NOTE: I'm not actually sure this is a great idea. -sd
(defmethod date-time java.time.Instant [i] i)
