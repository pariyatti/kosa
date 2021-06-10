(ns kosa.mobile.today.looped.db
  (:require [kuti.record :as record]
            [kuti.support.types :refer [namespace-kw]]
            [kuti.support.debugging :refer :all]))

(defn next-index [type]
  (let [find-query {:find     '[(max idx)]
                    :where    [['e (namespace-kw type :index) 'idx]]}
        result (-> (record/q find-query) first first)]
    (if result
      (+ 1 result)
      0)))
