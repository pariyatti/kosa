(ns kuti.record.publishing
  (:require [kuti.support.types :as types]
            [kuti.support.time :as time]))

(defn publish-at [e ts]
  (assoc e (types/typify e :published-at) (time/instant ts)))

(defn draft [e]
  (publish-at e time/DRAFT-DATE))

(defn publish [e]
  (if (get e (types/typify e :published-at))
    e
    (publish-at e (time/now))))

(defn republish [e]
  (publish-at e (time/now)))
