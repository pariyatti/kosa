(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list find get])
  (:require [kutis.record]))

(def fields #{:type
              :modified-at
              :original-url ;; from *.pariyatti.org
              :image-attachment-id})

(def attachment-fields #{:key :filename :content-type :metadata :service-name :byte-size :checksum})

(defn rehydrate [image]
  (let [attachment (kutis.record/get (:image-attachment-id image))]
    (assoc image :image-attachment attachment)))

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :type "image_artefact"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}
        raw-images (kutis.record/query list-query)]
    (map rehydrate raw-images)))

(defn find [match]
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :type "image_artefact"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}]
    (kutis.record/query list-query)))

;; TODO: extract "attachment-flattening" into its own ns.
(defn put [e]
  (let [attachment-doc (:image-attachment e)
        attachment (kutis.record/put attachment-doc attachment-fields)
        attachment-id (if attachment
                        (:crux.db/id attachment)
                        (throw (ex-info "Attachment not saved.")))
        artefact (-> e
                     (dissoc :image-attachment)
                     (assoc :image-attachment-id attachment-id))
        ;; TODO: we need a low-level home for applying `:modified-at` to all entities
        doc (assoc artefact
                   :modified-at (java.util.Date.)
                   :type "image_artefact")]
    (kutis.record/put doc fields)))

(defn get [id]
  (let [image (kutis.record/get id)
        attachment (kutis.record/get (:image-attachment-id image))]
    (assoc image :image-attachment attachment)))

(defn delete [e]
  (kutis.record/delete e))
