(ns kuti.fixtures.time-fixtures
  (:require [kuti.support.time :as time]))

(defn freeze-clock [t]
  (time/freeze-clock!)
  (t))

(def win95 (time/instant "1995-08-24T00:00:00.000Z"))

(defn freeze-clock-1995 [t]
  (time/freeze-clock! win95)
  (t))
