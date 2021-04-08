(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list find get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.search :as search]
            [kuti.storage :as storage]
            [clojure.string :as clojure.string]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]))

(defn rehydrate [image]
  (as-> (nested/expand-all image :image-artefact/image-attachment) img
    ;; TODO: this behaviour really belongs in kuti.storage
    (assoc-in img
              [:image-artefact/image-attachment :attm/url]
              (storage/url (:image-artefact/image-attachment img)))))

(defn list []
  (map rehydrate (record/list :image-artefact)))

(defn search-for [match]
  (if (= "" (clojure.string/trim match))
    []
    (let [matcher (format "%s*" match)
          list-query '{:find [?e ?v ?a ?s]
                       :in [?match]
	                     :where [[(wildcard-text-search ?match) [[?e ?v ?a ?s]]]
	                             [?e :crux.db/id]
                               [?e :kuti/type :image-artefact]]}
          raw-images (record/query list-query matcher)]
      (log/info (format "searching for '%s'" matcher))
      (map rehydrate raw-images))))

(defn save! [e]
  (-> e
      (assoc :kuti/type :image-artefact)
      (search/tag-searchables (-> e :image-artefact/image-attachment :attm/filename))
      (nested/collapse-one :image-artefact/image-attachment)
      record/timestamp
      record/publish
      record/save!))

(defn get [id]
  (rehydrate (record/get id)))

;; TODO: cascade record deletes to kuti.storage attachments, somehow?
;;       ...I actually think this might be too much work to bother doing. -sd
(defn delete [e]
  (record/delete e))
