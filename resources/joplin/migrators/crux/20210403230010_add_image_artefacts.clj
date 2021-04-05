(ns joplin.migrators.crux.20210403230010-add-image-artefacts
  (:require [crux.api :as x]
            [kuti.record.schema :as schema]
            [kuti.support.debugging :refer :all]
            [joplin.crux.database :as d]
            [tick.alpha.api :as t]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :image-artefact [:image-artefact/original-url
                                           :image-artefact/image-attachment-id
                                           :image-artefact/searchables
                                           :published-at])
    (schema/add-schema node :image-artefact/original-url        :db.type/uri)
    (schema/add-schema node :image-artefact/image-attachment-id :db.type/uuid)
    (schema/add-schema node :image-artefact/searchables         :db.type/string)
    (schema/add-schema node :published-at        :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :image-artefact)
    (schema/remove-schema node :image-artefact/original-url)
    (schema/remove-schema node :image-artefact/image-attachment-id)
    (schema/remove-schema node :image-artefact/searchables)
    (schema/remove-schema node :published-at)
    (d/close!)))
