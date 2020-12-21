(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list get])
  (:require [kosa.crux :as crux]))

(def fields [:type
             :modified-at
             :original-url ;; from *.pariyatti.org
             :filename
             :content-type
             :url          ;; content-addressable storage URL
             ])

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :type "image_artefact"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}]
    (crux/query list-query)))

(defn put [e]
  (crux/put e fields))

(defn put [e]
  ;; TODO: we need a low-level home for applying `:modified-at` to all entities
  (let [doc (assoc e
                   :modified-at (java.util.Date.)
                   :type "image_artefact")]
    (crux/put doc fields)))

(defn get [id]
  (crux/get id))

(defn delete [e]
  (crux/delete e))
