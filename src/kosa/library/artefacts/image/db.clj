(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list find get])
  (:require [kutis.record :as record]
            [kutis.record.nested :as nested]
            [kutis.search :as search]
            [kutis.storage :as storage]
            [clojure.string :as clojure.string]
            [clojure.tools.logging :as log]
            [kutis.support.time :as time]))

(def fields #{:type
              :modified-at
              :original-url ;; from *.pariyatti.org
              :image-attachment-id
              :searchables})

(defn rehydrate [image]
  (as-> (nested/expand-all image :image-attachment) img
      (assoc-in img
       [:image-attachment :url]
       (storage/url (:image-attachment img)))))

(defn list []
  (let [list-query '{:find     [e modified-at]
                     :where    [[e :type "image_artefact"]
                                [e :modified-at modified-at]]
                     :order-by [[modified-at :desc]]}
        raw-images (record/query list-query)]
    (map rehydrate raw-images)))

(defn search-for [match]
  (if (= "" (clojure.string/trim match))
    [] ;; TODO: return a 4xx error instead
    (let [matcher (format "%s*" match)
          list-query '{:find [?e ?v ?a ?s]
                       :in [?match]
	                     :where [[(wildcard-text-search ?match) [[?e ?v ?a ?s]]]
	                             [?e :crux.db/id]
                               [?e :type "image_artefact"]]}
          raw-images (record/query list-query matcher)]
      (log/info (format "searching for '%s'" matcher))
      (map rehydrate raw-images))))

(defn put [e]
  (let [doc (assoc e
                   :modified-at (time/now)
                   :type "image_artefact")]
    (-> doc
        (search/tag-searchables (-> doc :image-attachment :filename))
        (nested/collapse-one :image-attachment)
        (record/put fields))))

(defn get [id]
  (rehydrate (record/get id)))

;; TODO: cascade record deletes to kutis.storage attachments, somehow?
;;       ...I actually think this might be too much work to bother doing. -sd
(defn delete [e]
  (record/delete e))
