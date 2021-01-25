(ns kosa.mobile.today.stacked-inspiration.db
  (:refer-clojure :exclude [list get])
  (:require [kutis.record]))

(def fields #{:card-type :modified-at :published-at :bookmarkable :shareable
              :text :image-attachment})

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :card-type "stacked_inspiration"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}]
    (kutis.record/query list-query)))

(defn put [e]
  ;; TODO: we need a low-level home for applying `:modified-at` to all entities
  (kutis.record/put (assoc e :modified-at (java.util.Date.)) fields))

(defn get [id]
  (kutis.record/get id))
