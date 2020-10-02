(ns kosa-crux.routes
  (:require [ring.util.response :as resp]
            [bidi.bidi :as bidi]
            [bidi.ring]
            [kosa-crux.publisher.handler]
            [kosa-crux.publisher.entity.pali-word.spec]
            [kosa-crux.middleware :refer [wrap-spec-validation]]
            [kosa-crux.publisher.entity.pali-word.handler :as pali-word-handler]))

(defn not-found [_request]
  (resp/not-found {:message "not-found"}))

(defn pong [_request]
  (resp/response "pong"))

(def routes
  ["/" [["" (bidi.ring/->Redirect 307 kosa-crux.publisher.handler/index)]
        ["ping" pong]
        ["css" (bidi.ring/->Resources {:prefix "resources/public/css"})]
        ["js" (bidi.ring/->Resources {:prefix "resources/public/js"})]
        ["images" (bidi.ring/->Resources {:dir "resources/public/images"})]
        ["api/v1/today.json" pali-word-handler/list]

        ;; TODO: rails-ify / crud-ify / rest-ify resource routes
        ["publisher" [["" kosa-crux.publisher.handler/index]
                      ["/today" [["/pali_word_cards" pali-word-handler/index]
                                 ["/pali_word_card/new" pali-word-handler/new]
                                 [:post [[["/pali_word_card"] (wrap-spec-validation :entity/pali-word-request pali-word-handler/create)]]]
                                 [:get  [[["/pali_word_card/" :id] pali-word-handler/show]]]]]]]

        [true not-found]]])

;; example crud-ful routes:

;; GET    /publisher/cards/pali_word_cards(.:format)                    cards/pali_word_cards#index
;; POST   /publisher/cards/pali_word_cards(.:format)                    cards/pali_word_cards#create
;; GET    /publisher/cards/pali_word_cards/new(.:format)                cards/pali_word_cards#new
;; GET    /publisher/cards/pali_word_cards/:id/edit(.:format)           cards/pali_word_cards#edit
;; GET    /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#show
;; PATCH  /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#update
;; PUT    /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#update
;; DELETE /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#destroy
