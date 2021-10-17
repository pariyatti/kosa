(ns joplin.migrators.xtdb.20210402000000-add-image-artefacts
  (:require [xtdb.api :as xt]
            [kuti.record.schema :as schema]
            [kuti.support.debugging :refer :all]
            [joplin.xtdb.database :as d]
            [tick.alpha.api :as t]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :image-artefact [:image-artefact/original-url
                                           :image-artefact/image-attachment-id
                                           :image-artefact/searchables
                                           :image-artefact/published-at])
    (schema/add-schema node :image-artefact/original-url        :db.type/uri)
    (schema/add-schema node :image-artefact/image-attachment-id :db.type/uuid)
    (schema/add-schema node :image-artefact/searchables         :db.type/string)
    (schema/add-schema node :image-artefact/published-at        :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :image-artefact)
    (schema/remove-schema node :image-artefact/original-url)
    (schema/remove-schema node :image-artefact/image-attachment-id)
    (schema/remove-schema node :image-artefact/searchables)
    (schema/remove-schema node :image-artefact/published-at)
    (d/close!)))
