(ns kosa-crux.publisher.entity.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kosa-crux.crux :as crux]))

(def fields [:card-type :published-at :bookmarkable :shareable :pali])

(defn list []
  (let [list-pali-words-query '{:find     [e published-at]
                                :where    [[e :card-type "pali_word"]
                                           [e :published-at published-at]]
                                :order-by [[published-at :desc]]}]
    (crux/query list-pali-words-query)))

(defn put [e]
  (crux/put e fields))

(defn sync-put [e]
  (crux/sync-put e fields))

(defn get [id]
  (crux/get id))
