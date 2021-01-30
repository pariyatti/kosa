(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list find get])
  (:require [kutis.record]))

(def fields #{:type
              :modified-at
              :original-url ;; from *.pariyatti.org
              :image-attachment-id
              :searchables})

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

(defn search [match]
  (let [matcher (format "%s*" match)
        list-query '{:find [?e ?v ?a ?s]
                     :in [?match]
	                   :where [[(wildcard-text-search ?match) [[?e ?v ?a ?s]]]
	                           [?e :crux.db/id]
                             [?e :type "image_artefact"]]}
        raw-images (kutis.record/query list-query matcher)]
    (prn (format "searching for '%s'" matcher))
    (map rehydrate raw-images)))

(defn tag-searchables [e string]
  (let [searchables (clojure.string/split string #"-|_|~|=|\$|\{|\}|\.|\[|\]|\+")
        searchables (conj searchables string)
        searchable-string (clojure.string/join " " searchables)]
    (assoc e :searchables searchable-string)))

;; TODO: extract "attachment-flattening" into its own ns.
(defn put [e]
  (let [attachment-doc (:image-attachment e)
        attachment (kutis.record/put attachment-doc attachment-fields)
        attachment-id (if attachment
                        (:crux.db/id attachment)
                        (throw (ex-info "Attachment not saved.")))
        artefact (-> e
                     (tag-searchables (:filename attachment))
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

;; TODO: cascade record deletes to kutis.storage attachments, somehow?
;;       ...I actually think this might be too much work to bother doing. -sd
(defn delete [e]
  (kutis.record/delete e))
