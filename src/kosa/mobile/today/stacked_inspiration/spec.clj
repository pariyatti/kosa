(ns kosa.mobile.today.stacked-inspiration.spec
  (:require [clojure.string]
            [clojure.spec.alpha :as s]))

(s/def :entity/truthy-string (s/and string? #(or (= "true" %)
                                                 (= "false" %))))
(s/def :entity/truthy-value boolean?)

(s/def :stacked-inspiration/published-at inst?)
(s/def :stacked-inspiration/bookmarkable (s/or :entity/truthy-value :entity/truthy-string))
(s/def :stacked-inspiration/shareable (s/or :entity/truthy-value :entity/truthy-string))
(s/def :stacked-inspiration/card-type (s/and string? #(= "stacked_inspiration" %)))
(s/def :stacked-inspiration/text (s/and string? #(-> % clojure.string/blank? not)))
(s/def :stacked-inspiration/id uuid?)
(s/def :stacked-inspiration/header
  (s/and string? #(-> % clojure.string/blank? not)))

(s/def :entity/stacked-inspiration
  (s/keys :req-un [:stacked-inspiration/id
                   :stacked-inspiration/header
                   :stacked-inspiration/bookmarkable
                   :stacked-inspiration/shareable
                   :stacked-inspiration/card-type
                   :stacked-inspiration/text
                   :stacked-inspiration/published-at]))

(s/def :entity/stacked-inspiration-request
  (s/keys :req-un [:stacked-inspiration/header
                   :stacked-inspiration/text]
          :opt-un [:stacked-inspiration/bookmarkable
                   :stacked-inspiration/shareable]))
