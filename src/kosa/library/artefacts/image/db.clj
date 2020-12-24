(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list get])
  (:require [kutis.record]))

(def fields [:type
             :modified-at
             :original-url ;; from *.pariyatti.org
             :attached-image
             ])

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :type "image_artefact"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}]
    (kutis.record/query list-query)))

(defn put [e]
  (kutis.record/put e fields))

(defn put [e]
  ;; TODO: we need a low-level home for applying `:modified-at` to all entities
  (let [doc (assoc e
                   :modified-at (java.util.Date.)
                   :type "image_artefact")]
    (kutis.record/put doc fields)))

(defn get [id]
  (kutis.record/get id))

(defn delete [e]
  (kutis.record/delete e))
