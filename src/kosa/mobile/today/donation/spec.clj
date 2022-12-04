(ns kosa.mobile.today.donation.spec
  (:require [clojure.string]
            [clojure.spec.alpha :as s]))

(s/def :entity/truthy-string (s/and string? #(or (= "true" %)
                                                 (= "false" %))))
(s/def :entity/truthy-value boolean?)

(s/def :donation/published-at inst?)
(s/def :donation/card-type (s/and string? #(= "donation" %)))
(s/def :donation/id uuid?)
(s/def :donation/header (s/and string? #(-> % clojure.string/blank? not)))
(s/def :donation/title (s/and string? #(-> % clojure.string/blank? not)))
(s/def :donation/text (s/and string? #(-> % clojure.string/blank? not)))
(s/def :donation/button (s/and string? #(-> % clojure.string/blank? not)))

(s/def :entity/donation
  (s/keys :req-un [:donation/id
                   :donation/header
                   :donation/title
                   :donation/card-type
                   :donation/text
                   :donation/published-at]))

(s/def :entity/donation-request
  (s/keys :req-un [:donation/title
                   :donation/text]))
