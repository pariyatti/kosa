(ns kosa-crux.publisher.entity.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kosa-crux.crux :as crux]))

(def fields [:card-type :modified-at :published-at :bookmarkable :shareable :pali])

(defn list []
  (let [list-pali-words-query '{:find     [e modified-at]
                                :where    [[e :card-type "pali_word"]
                                           [e :modified-at modified-at]]
                                :order-by [[modified-at :desc]]}]
    (crux/query list-pali-words-query)))

(defn put [e]
  (crux/put e fields))

(defn sync-put [e]
  ;; TODO: we need a low-level home for applying `:modified-at` to all entities
  (crux/sync-put (assoc e :modified-at (java.util.Date.)) fields))

(defn get [id]
  (crux/get id))
