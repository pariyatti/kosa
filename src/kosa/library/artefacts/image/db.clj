(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list find get])
  (:require [kutis.record :as record]
            [kutis.record.nested :as nested]
            [kutis.search :as search]))

(def fields #{:type
              :modified-at
              :original-url ;; from *.pariyatti.org
              :image-attachment-id
              :searchables})

;; TODO: remove this:
(def attachment-fields #{:key :filename :content-type :metadata :service-name :byte-size :checksum})

(defn rehydrate [image]
  (let [attachment (record/get (:image-attachment-id image))]
    (assoc image :image-attachment attachment)))

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :type "image_artefact"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}
        raw-images (record/query list-query)]
    (map rehydrate raw-images)))

(defn search-for [match]
  (let [matcher (format "%s*" match)
        list-query '{:find [?e ?v ?a ?s]
                     :in [?match]
	                   :where [[(wildcard-text-search ?match) [[?e ?v ?a ?s]]]
	                           [?e :crux.db/id]
                             [?e :type "image_artefact"]]}
        raw-images (record/query list-query matcher)]
    (prn (format "searching for '%s'" matcher))
    (map rehydrate raw-images)))

(defn put [e]
  (let [doc (assoc e
                   :modified-at (java.util.Date.)
                   :type "image_artefact")]
    (-> doc
        (search/tag-searchables (-> doc :image-attachment :filename))
        (nested/collapse-one :image-attachment)
        (record/put fields))))

(defn get [id]
  (let [image (record/get id)
        attachment (record/get (:image-attachment-id image))]
    (assoc image :image-attachment attachment)))

;; TODO: cascade record deletes to kutis.storage attachments, somehow?
;;       ...I actually think this might be too much work to bother doing. -sd
(defn delete [e]
  (record/delete e))
