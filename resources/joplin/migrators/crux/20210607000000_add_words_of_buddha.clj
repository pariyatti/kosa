(ns joplin.migrators.crux.20210607000000-add-words-of-buddha
  (:require [joplin.crux.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :words-of-buddha [:words-of-buddha/original-words ;; from *.pariyatti.org - a long string
                                            :words-of-buddha/original-url   ;; from *.pariyatti.org
                                            :words-of-buddha/words
                                            :words-of-buddha/audio-attachment-id
                                            :words-of-buddha/audio-url      ;; to *.pariyatti.org - mp3
                                            :words-of-buddha/translations
                                            :words-of-buddha/citepali
                                            :words-of-buddha/citepali-url
                                            :words-of-buddha/citebook
                                            :words-of-buddha/citebook-url      ;; to store.pariyatti.org
                                            :words-of-buddha/published-at])

    (schema/add-schema node :words-of-buddha/original-words :db.type/string)
    (schema/add-schema node :words-of-buddha/original-url   :db.type/uri)
    (schema/add-schema node :words-of-buddha/words          :db.type/string)
    (schema/add-schema node :words-of-buddha/audio-attachment-id  :db.type/uuid)
    (schema/add-schema node :words-of-buddha/audio-url      :db.type/uri)
    (schema/add-schema node :words-of-buddha/translations   :db.type/tuple)
    (schema/add-schema node :words-of-buddha/citepali       :db.type/string)
    (schema/add-schema node :words-of-buddha/citepali-url   :db.type/uri)
    (schema/add-schema node :words-of-buddha/citebook    :db.type/string)
    (schema/add-schema node :words-of-buddha/citebook-url      :db.type/uri)
    (schema/add-schema node :words-of-buddha/published-at   :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :words-of-buddha)
    (schema/remove-schema node :words-of-buddha/original-words)
    (schema/remove-schema node :words-of-buddha/original-url)
    (schema/remove-schema node :words-of-buddha/words)
    (schema/remove-schema node :words-of-buddha/audio-attachment-id)
    (schema/remove-schema node :words-of-buddha/audio-url)
    (schema/remove-schema node :words-of-buddha/translations)
    (schema/remove-schema node :words-of-buddha/citepali)
    (schema/remove-schema node :words-of-buddha/citepali-url)
    (schema/remove-schema node :words-of-buddha/citebook)
    (schema/remove-schema node :words-of-buddha/citebook-url)
    (schema/remove-schema node :words-of-buddha/published-at)
    (d/close!)))
