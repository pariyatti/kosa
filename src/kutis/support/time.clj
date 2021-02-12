(ns kutis.support.time
  (:refer-clojure :exclude [format])
  (:require [tick.alpha.api :as t]
            [clojure.string :as clojure.string]))

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
