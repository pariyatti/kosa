(ns joplin.migrators.xtdb.20221204000000-add-donation-card
  (:require [joplin.xtdb.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :donation [:donation/header
                                     :donation/title
                                     :donation/text
                                     :donation/image-attachment-id
                                     :donation/button
                                     :donation/published-at])

    (schema/add-schema node :donation/header              :db.type/string)
    (schema/add-schema node :donation/title               :db.type/string)
    (schema/add-schema node :donation/text                :db.type/string)
    (schema/add-schema node :donation/image-attachment-id :db.type/uuid)
    (schema/add-schema node :donation/button              :db.type/string)
    (schema/add-schema node :donation/published-at        :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :donation)
    (schema/remove-schema node :donation/header)
    (schema/remove-schema node :donation/title)
    (schema/remove-schema node :donation/text)
    (schema/remove-schema node :donation/image-attachment-id)
    (schema/remove-schema node :donation/button)
    (schema/remove-schema node :donation/published-at)
    (d/close!)))
