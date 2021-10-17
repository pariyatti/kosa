(ns joplin.migrators.xtdb.20210422000000-add-looped-pali-word
  (:require [joplin.xtdb.database :as d]
            [kuti.support.debugging :refer :all]
            [kuti.record.schema :as schema]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (schema/add-type node :looped-pali-word [:looped-pali-word/index
                                             :looped-pali-word/original-pali  ;; from *.pariyatti.org - a long string
                                             :looped-pali-word/original-url   ;; from *.pariyatti.org
                                             :looped-pali-word/pali
                                             :looped-pali-word/translations
                                             :looped-pali-word/published-at])
    (schema/add-schema node :looped-pali-word/index         :db.type/long)
    (schema/add-schema node :looped-pali-word/original-pali :db.type/string)
    (schema/add-schema node :looped-pali-word/original-url  :db.type/uri)
    (schema/add-schema node :looped-pali-word/pali          :db.type/string)
    (schema/add-schema node :looped-pali-word/translations  :db.type/tuple)
    (schema/add-schema node :looped-pali-word/published-at  :db.type/inst)
    (d/close!)))

(defn down [db]
  (let [node (d/get-node (:conf db))]
    (schema/remove-type node :looped-pali-word)
    (schema/remove-schema node :looped-pali-word/index)
    (schema/remove-schema node :looped-pali-word/original-pali)
    (schema/remove-schema node :looped-pali-word/original-url)
    (schema/remove-schema node :looped-pali-word/pali)
    (schema/remove-schema node :looped-pali-word/translations)
    (schema/remove-schema node :looped-pali-word/published-at)
    (d/close!)))
