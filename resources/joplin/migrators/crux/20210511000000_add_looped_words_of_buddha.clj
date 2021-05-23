(ns joplin.migrators.crux.20210511000000-add-looped-words-of-buddha
  (:require [joplin.crux.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :looped-words-of-buddha [:looped-words-of-buddha/index
                                                   :looped-words-of-buddha/original-words ;; from *.pariyatti.org - a long string
                                                   :looped-words-of-buddha/original-url   ;; from *.pariyatti.org
                                                   :looped-words-of-buddha/bookmarkable
                                                   :looped-words-of-buddha/shareable
                                                   :looped-words-of-buddha/words
                                                   :looped-words-of-buddha/audio-attachment-id
                                                   :looped-words-of-buddha/audio-url      ;; to *.pariyatti.org - mp3
                                                   :looped-words-of-buddha/translations
                                                   :looped-words-of-buddha/citation
                                                   :looped-words-of-buddha/citation-url
                                                   :looped-words-of-buddha/store-title
                                                   :looped-words-of-buddha/store-url      ;; to store.pariyatti.org
                                                   :looped-words-of-buddha/published-at])

    (schema/add-schema node :looped-words-of-buddha/index          :db.type/long)
    (schema/add-schema node :looped-words-of-buddha/original-words :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/original-url   :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/bookmarkable   :db.type/boolean)
    (schema/add-schema node :looped-words-of-buddha/shareable      :db.type/boolean)
    (schema/add-schema node :looped-words-of-buddha/words          :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/audio-attachment-id  :db.type/uuid)
    (schema/add-schema node :looped-words-of-buddha/audio-url      :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/translations   :db.type/tuple)
    (schema/add-schema node :looped-words-of-buddha/citation       :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/citation-url   :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/store-title    :db.type/string)
    (schema/add-schema node :looped-words-of-buddha/store-url      :db.type/uri)
    (schema/add-schema node :looped-words-of-buddha/published-at   :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :looped-words-of-buddha)
    (schema/remove-schema node :looped-words-of-buddha/index)
    (schema/remove-schema node :looped-words-of-buddha/original-words)
    (schema/remove-schema node :looped-words-of-buddha/original-url)
    (schema/remove-schema node :looped-words-of-buddha/bookmarkable)
    (schema/remove-schema node :looped-words-of-buddha/shareable)
    (schema/remove-schema node :looped-words-of-buddha/words)
    (schema/remove-schema node :looped-words-of-buddha/audio-attachment-id)
    (schema/remove-schema node :looped-words-of-buddha/audio-url)
    (schema/remove-schema node :looped-words-of-buddha/translations)
    (schema/remove-schema node :looped-words-of-buddha/citation)
    (schema/remove-schema node :looped-words-of-buddha/citation-url)
    (schema/remove-schema node :looped-words-of-buddha/store-title)
    (schema/remove-schema node :looped-words-of-buddha/store-url)
    (schema/remove-schema node :looped-words-of-buddha/published-at)
    (d/close!)))
