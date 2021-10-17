(ns joplin.migrators.xtdb.20210404000000-add-stacked-inspiration
  (:require [joplin.xtdb.database :as d]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node
                     :stacked-inspiration
                     [:stacked-inspiration/image-attachment-id
                      :stacked-inspiration/text
                      :stacked-inspiration/published-at])
    (schema/add-schema node :stacked-inspiration/image-attachment-id :db.type/uuid)
    (schema/add-schema node :stacked-inspiration/text                :db.type/string)
    (schema/add-schema node :stacked-inspiration/published-at        :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :stacked-inspiration)
    (schema/remove-schema node :stacked-inspiration/image-attachment-id)
    (schema/remove-schema node :stacked-inspiration/text)
    (schema/remove-schema node :stacked-inspiration/published-at)
    (d/close!)))
