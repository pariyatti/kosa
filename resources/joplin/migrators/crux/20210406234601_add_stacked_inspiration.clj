(ns joplin.migrators.crux.20210406234601-add-stacked-inspiration
  (:require [joplin.crux.database :as d]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node
                     :stacked-inspiration
                     [:stacked-inspiration/image-attachment-id
                      :stacked-inspiration/bookmarkable
                      :stacked-inspiration/shareable
                      :stacked-inspiration/text
                      :published-at])
    (schema/add-schema node :stacked-inspiration/image-attachment-id :db.type/uuid)
    (schema/add-schema node :stacked-inspiration/bookmarkable        :db.type/boolean)
    (schema/add-schema node :stacked-inspiration/shareable           :db.type/boolean)
    (schema/add-schema node :stacked-inspiration/text                :db.type/string)
    (schema/add-schema node :published-at                            :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :stacked-inspiration)
    (schema/remove-schema node :stacked-inspiration/image-attachment-id)
    (schema/remove-schema node :stacked-inspiration/bookmarkable)
    (schema/remove-schema node :stacked-inspiration/shareable)
    (schema/remove-schema node :stacked-inspiration/text)
    (schema/remove-schema node :published-at)
    (d/close!)))
