(ns joplin.migrators.xtdb.20210612000000-add-doha
  (:require [joplin.xtdb.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :doha [:doha/original-doha      ;; from *.pariyatti.org - a long string
                                 :doha/original-url       ;; from *.pariyatti.org
                                 :doha/original-audio-url ;; to *.pariyatti.org - mp3
                                 :doha/doha
                                 :doha/audio-attachment-id
                                 :doha/translations
                                 :doha/published-at])

    (schema/add-schema node :doha/original-doha  :db.type/string)
    (schema/add-schema node :doha/original-url   :db.type/uri)
    (schema/add-schema node :doha/original-audio-url      :db.type/uri)
    (schema/add-schema node :doha/doha           :db.type/string)
    (schema/add-schema node :doha/audio-attachment-id  :db.type/uuid)
    (schema/add-schema node :doha/translations   :db.type/tuple)
    (schema/add-schema node :doha/published-at   :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :doha)
    (schema/remove-schema node :doha/original-doha)
    (schema/remove-schema node :doha/original-url)
    (schema/remove-schema node :doha/original-audio-url)
    (schema/remove-schema node :doha/doha)
    (schema/remove-schema node :doha/audio-attachment-id)
    (schema/remove-schema node :doha/translations)
    (schema/remove-schema node :doha/published-at)
    (d/close!)))
