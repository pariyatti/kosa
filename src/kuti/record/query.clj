(ns kuti.record.query
  (:require [kuti.support.types :refer [namespace-kw]]
            [kuti.record.core :as record]
            [kuti.storage.nested :refer [expand-all]]))

(defn find-all [type attr param]
  (let [find-query {:find     '[e updated-at]
                    :where    [['e attr 'v]
                               ['e (namespace-kw type :updated-at) 'updated-at]]
                    :order-by '[[updated-at :desc]]
                    :in       '[v]}]
    (map expand-all (record/query find-query param))))
