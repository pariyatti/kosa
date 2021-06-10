(ns kosa.mobile.today.looped-words-of-buddha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.storage.nested :refer [expand-all]]
            [kuti.support :refer [assoc-unless]]
            [kuti.support.debugging :refer :all]
            [kosa.mobile.today.looped.db :refer [next-index]]))

(defn list []
  (map expand-all (record/list :looped-words-of-buddha)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :where    [['e attr 'v]
                               '[e :looped-words-of-buddha/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]
                    :in       '[v]}]
    (map expand-all (record/query find-query param))))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-words-of-buddha)
      (assoc-unless :looped-words-of-buddha/index (next-index :looped-words-of-buddha))
      (nested/collapse-one :looped-words-of-buddha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))
