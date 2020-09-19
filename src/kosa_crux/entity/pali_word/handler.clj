(ns kosa-crux.entity.pali-word.handler
  (:refer-clojure :exclude [list])
  (:require [clojure.spec.alpha :as s]
            [clojure.string]
            [ring.util.response :as resp]
            [kosa-crux.entity.pali-word.db :as pali-word-db]))

(s/def :pali-word/published-at inst?)
(s/def :pali-word/bookmarkable boolean?)
(s/def :pali-word/shareable boolean?)
(s/def :pali-word/type (s/and string? #(= "pali_word" %)))
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
                   :pali-word/type
                   :pali-word/pali
                   :pali-word/published-at
                   :pali-word/audio
                   :pali-word/translations]))

(defn list []
  (resp/response
   (pali-word-db/list)))
