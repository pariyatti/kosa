(ns joplin.migrators.xtdb.20210511000000-add-looped-words-of-buddha
  (:require [joplin.xtdb.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :looped-words-of-buddha [:looped-words-of-buddha/index
                                                   :looped-words-of-buddha/original-words ;; from *.pariyatti.org - a long string
                                                   :looped-words-of-buddha/original-url   ;; from *.pariyatti.org
                                                   :looped-words-of-buddha/original-audio-url      ;; to *.pariyatti.org - mp3
                                                   :looped-words-of-buddha/words
                                                   :looped-words-of-buddha/audio-attachment-id
                                                   :looped-words-of-buddha/translations
                                                   :looped-words-of-buddha/citepali
                                                   :looped-words-of-buddha/citepali-url
                                                   :looped-words-of-buddha/citebook
                                                   :looped-words-of-buddha/citebook-url      ;; to store.pariyatti.org
                                                   :looped-words-of-buddha/published-at])

    (schema/add-schema node :looped-words-of-buddha/index               :db.type/long)
    (schema/add-schema node :looped-words-of-buddha/original-words      :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/original-url        :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/original-audio-url  :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/words               :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/audio-attachment-id :db.type/uuid)
    (schema/add-schema node :looped-words-of-buddha/translations        :db.type/tuple)
    (schema/add-schema node :looped-words-of-buddha/citepali            :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/citepali-url        :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/citebook            :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/citebook-url        :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/published-at        :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :looped-words-of-buddha)
    (schema/remove-schema node :looped-words-of-buddha/index)
    (schema/remove-schema node :looped-words-of-buddha/original-words)
    (schema/remove-schema node :looped-words-of-buddha/original-url)
    (schema/remove-schema node :looped-words-of-buddha/original-audio-url)
    (schema/remove-schema node :looped-words-of-buddha/words)
    (schema/remove-schema node :looped-words-of-buddha/audio-attachment-id)
    (schema/remove-schema node :looped-words-of-buddha/translations)
    (schema/remove-schema node :looped-words-of-buddha/citepali)
    (schema/remove-schema node :looped-words-of-buddha/citepali-url)
    (schema/remove-schema node :looped-words-of-buddha/citebook)
    (schema/remove-schema node :looped-words-of-buddha/citebook-url)
    (schema/remove-schema node :looped-words-of-buddha/published-at)
    (d/close!)))
