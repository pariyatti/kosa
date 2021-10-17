(ns joplin.migrators.xtdb.20210609000000-add-looped-doha
  (:require [joplin.xtdb.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :looped-doha [:looped-doha/index
                                        :looped-doha/original-doha  ;; from *.pariyatti.org - a long string
                                        :looped-doha/original-url   ;; from *.pariyatti.org
                                        :looped-doha/doha
                                        :looped-doha/audio-attachment-id
                                        :looped-doha/audio-url      ;; to *.pariyatti.org - mp3
                                        :looped-doha/translations
                                        :looped-doha/published-at])

    (schema/add-schema node :looped-doha/index          :db.type/long)
    (schema/add-schema node :looped-doha/original-doha  :db.type/string)
    (schema/add-schema node :looped-doha/original-url   :db.type/uri)
    (schema/add-schema node :looped-doha/doha           :db.type/string)
    (schema/add-schema node :looped-doha/audio-attachment-id  :db.type/uuid)
    (schema/add-schema node :looped-doha/audio-url      :db.type/uri)
    (schema/add-schema node :looped-doha/translations   :db.type/tuple)
    (schema/add-schema node :looped-doha/published-at   :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :looped-doha)
    (schema/remove-schema node :looped-doha/index)
    (schema/remove-schema node :looped-doha/original-doha)
    (schema/remove-schema node :looped-doha/original-url)
    (schema/remove-schema node :looped-doha/doha)
    (schema/remove-schema node :looped-doha/audio-attachment-id)
    (schema/remove-schema node :looped-doha/audio-url)
    (schema/remove-schema node :looped-doha/translations)
    (schema/remove-schema node :looped-doha/published-at)
    (d/close!)))
