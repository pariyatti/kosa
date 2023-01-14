(ns kuti.support.time
  (:refer-clojure :exclude [time])
  (:require [tick.alpha.api :as t]
            [chime.core :as chime]
            [clojure.string :as s]
            [kuti.support.strings :refer [slice]])
  (:import [java.time LocalDate ZonedDateTime ZoneId]
           [java.time.chrono Era IsoEra IsoChronology]))

(def ^:dynamic clock (t/atom))

(defn freeze-clock!
  "THIS IS ONLY FOR TESTS."
  ([]
   (freeze-clock! (t/now)))
  ([i]
   (let [inst (t/instant i)]
     (t/reset! clock (t/clock inst))
     inst)))

(defn unfreeze-clock!
  "THIS IS ONLY FOR TESTS."
  []
  (t/reset! clock (t/clock)))

(defn now []
  @clock)

(defn instant
  "This fn is a little silly, but using `kuti.support.time`
   exclusively ensures we don't accidentally consume the more
   powerful features from `tick`."
  [time]
  (t/instant time))

(defn mask-str [sz mask]
  (apply str
         (concat (seq sz)
                 (drop (count sz) (seq mask)))))

(defn includes-any? [sz substrs]
  (->> substrs
       (map (partial s/includes? sz))
       (some true?)))

(defn parse
  "This fn intentionally does not understand local dates/times
   at all."
  [sz]
  (when (or (includes-any? sz ["[" "]"])
            (re-matches #".*-\d\d:\d\d$" sz)
            (re-matches #".*\+\d\d:\d\d$" sz))
    (throw (IllegalArgumentException. "Localized date-times not permitted.")))
  (-> sz
      (s/replace #"Z" "")
      (slice 0 23)
      (mask-str "0000-00-00T00:00:00.000Z")
      t/parse
      t/instant))

(defn parse-tz
  "Try to avoid this fn unless parsing external dates known to
   contain a timezone."
  [sz]
  (t/parse sz))

(defn string
  "Force the `yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format."
  [time]
  (let [inst (t/instant time)
        sz (str inst)]
    (if (= 24 (count sz))
      sz
      (s/replace sz #"Z$" ".000Z"))))

(def f8601-0 #"\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\dZ")
(def f8601-3 #"\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d.\d\d\dZ")
(def f8601-6 #"\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d.\d\d\d\d\d\dZ")
(def f8601-9 #"\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d.\d\d\d\d\d\d\d\d\dZ")

(defn to-8601-string
  "Force the `yyyy-MM-dd'T'HH:mm:ssSSS'Z' format."
  [time]
  (let [inst (t/instant time)
        sz (str inst)]
    (cond
      (re-matches f8601-0 sz) (s/replace sz #"Z$" ".000Z")
      (re-matches f8601-3 sz) sz
      ;; technically 6-digit and 9-digit representations shouldn't be possible
      ;; but we cover them anyway to be explicit about our intention:
      (re-matches f8601-6 sz) (s/replace sz #"(\.\d\d\d)\d\d\dZ" "$1Z")
      (re-matches f8601-9 sz) (s/replace sz #"(\.\d\d\d)\d\d\d\d\d\dZ" "$1Z")
      :else sz)))

(defn days-between [old new]
  (t/days (t/between (parse (str old))
                     (parse (str new)))))

(defn extract-date [inst]
  (-> (str inst) (clojure.string/split #"T") first t/date))

(defn at [d time]
  (t/at d time))

(defn to-utc [ldt]
  (-> ldt (str "Z") (t/instant)))

(defn schedule [offset-seconds period-seconds]
  (-> (chime/periodic-seq (t/>> (now) (t/new-duration offset-seconds :seconds))
                          (t/new-duration period-seconds :seconds))
      rest))

;; dates and publishing:

(def DRAFT-DATE (instant "9999-01-01T00:00:00.000Z"))
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
  "Always use this public API to create :sometype/published-at date-times."
  (fn [d & args] (class d)))

(defmethod date-time java.time.LocalDate
  ([d]   (date-time d (time 0 0 0)))
  ([d t] (t/instant (ZonedDateTime/of d t (ZoneId/of "UTC")))))

;; NOTE: I'm not actually sure this is a great idea. -sd
(defmethod date-time java.time.Instant [i] i)

(defn to-no-timezone
  "When publishing, cheat and treat UTC as if it were 'no timezone'."
  [ldt]
  (to-utc ldt))

(defn pst-to-utc
  "When publishing, use a 'no timezone' time at PST and shift into UTC."
  [old]
  (t/>> old
        (t/new-duration 8 :hours)))
