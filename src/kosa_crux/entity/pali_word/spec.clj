(ns kosa-crux.entity.pali-word.spec
  (:require [clojure.string]
            [clojure.spec.alpha :as s]))

(s/def :entity/truthy-string (s/and string? #(or (= "true" %)
                                                 (= "false" %))))
(s/def :entity/truthy-value boolean?)

(s/def :pali-word/published-at inst?)
(s/def :pali-word/bookmarkable (s/or :entity/truthy-value :entity/truthy-string))
(s/def :pali-word/shareable (s/or :entity/truthy-value :entity/truthy-string))
(s/def :pali-word/card-type (s/and string? #(= "pali_word" %)))
(s/def :pali-word/pali (s/and string? #(-> % clojure.string/blank? not)))
(s/def :pali-word/id uuid?)
(s/def :pali-word/header
  (s/and string? #(-> % clojure.string/blank? not)))

(s/def :pali-word.audio/url (s/and string? #(-> % clojure.string/blank? not)))
(s/def :pali-word/audio (s/keys :req-un [:pali-word.audio/url]))

(s/def :translations/language (s/and string? #(contains? #{"hi" "en"} %)))
(s/def :translations/translation (s/and string? #(-> % clojure.string/blank? not)))
(s/def :translations/id uuid?)
(s/def :pali-word/translations
  (s/coll-of (s/keys :req-un [:translations/id
                              :translations/translation
                              :translations/language])))

(s/def :entity/pali-word
  (s/keys :req-un [:pali-word/id
                   :pali-word/header
                   :pali-word/bookmarkable
                   :pali-word/shareable
                   :pali-word/card-type
                   :pali-word/pali
                   :pali-word/published-at
                   :pali-word/audio
                   :pali-word/translations]))

(s/def :entity/pali-word-request
  (s/keys :req-un [:pali-word/header
                   :pali-word/pali]
          :opt-un [:pali-word/bookmarkable
                   :pali-word/shareable]))
