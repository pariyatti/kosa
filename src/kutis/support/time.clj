(ns kutis.support.time
  (:refer-clojure :exclude [format])
  (:require [tick.alpha.api :as t]))

(defn format2 [time]
  ;; TODO: announcing when we are in UTC when we are not is obviously wrong
  ;;       ...we should set the timezone on times to UTC
  (t/format (tick.format/formatter "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") time))

(defprotocol FormatDate
  (fmt [date]))

(extend nil
  FormatDate
  {:fmt (fn [_] nil)})

(extend java.util.Date
  FormatDate
  {:fmt (fn [inst]
          (format2 (t/zoned-date-time inst)))})

(extend java.lang.String
  FormatDate
  {:fmt (fn [s]
          (format2 (t/parse s)))})
