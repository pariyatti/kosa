(ns kosa.library.artefacts.image.db
  (:refer-clojure :exclude [list find get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.search :as search]
            [kuti.storage.nested :refer [expand-all]]
            [clojure.string :as clojure.string]
            [clojure.tools.logging :as log]
            [kuti.support.debugging :refer :all]))

(defn list []
  (map expand-all (record/list :image-artefact)))

(defn search-for [match]
  (if (= "" (clojure.string/trim match))
    []
    (let [matcher (format "%s*" match)
          list-query '{:find [?e ?v ?a ?s]
                       :in [?match]
	                     :where [[(wildcard-text-search ?match) [[?e ?v ?a ?s]]]
	                             [?e :xt/id]
                               [?e :kuti/type :image-artefact]]}
          raw-images (record/query list-query matcher)]
      (log/info (format "searching for '%s'" matcher))
      (map expand-all raw-images))))

(defn save! [e]
  (-> e
      (assoc :kuti/type :image-artefact)
      (search/tag-searchables (-> e :image-artefact/image-attachment :attm/filename))
      (nested/collapse-one :image-artefact/image-attachment)
      record/timestamp
      record/publish
      record/save!))

(defn get [id]
  (expand-all (record/get id)))

;; TODO: cascade record deletes to kuti.storage attachments, somehow?
;;       ...I actually think this might be too much work to bother doing. -sd
(defn delete [e]
  (record/delete e))
