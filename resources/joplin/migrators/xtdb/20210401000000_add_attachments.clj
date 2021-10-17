(ns joplin.migrators.xtdb.20210401000000-add-attachments
  (:require [joplin.xtdb.database :as d]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :attm [:attm/key
                                 :attm/filename
                                 :attm/content-type
                                 :attm/metadata
                                 :attm/service-name
                                 :attm/byte-size
                                 :attm/checksum
                                 :attm/identified])
    (schema/add-schema node :attm/key          :db.type/string)
    (schema/add-schema node :attm/filename     :db.type/string)
    (schema/add-schema node :attm/content-type :db.type/string)
    (schema/add-schema node :attm/metadata     :db.type/string)
    (schema/add-schema node :attm/service-name :db.type/keyword)
    (schema/add-schema node :attm/byte-size    :db.type/bigint)
    (schema/add-schema node :attm/checksum     :db.type/string)
    (schema/add-schema node :attm/identified   :db.type/boolean)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :attm)
    (schema/remove-schema node :attm/key)
    (schema/remove-schema node :attm/filename)
    (schema/remove-schema node :attm/content-type)
    (schema/remove-schema node :attm/metadata)
    (schema/remove-schema node :attm/service-name)
    (schema/remove-schema node :attm/byte-size)
    (schema/remove-schema node :attm/checksum)
    (schema/remove-schema node :attm/identified)
    (d/close!)))
