(ns kosa-crux.routes
  (:require [ring.util.response :as resp]
            [reitit.ring :as rr]
            [kosa-crux.publisher.handler]
            [kosa-crux.publisher.entity.pali-word.spec]
            [kosa-crux.middleware :refer [wrap-spec-validation]]
            [kosa-crux.publisher.entity.pali-word.handler :as pali-word-handler]))

(defn pong [_request]
  (resp/response "pong"))

(defn redirect [location]
  {:status  307
   :headers {"Location" location}
   :body    (str "Redirect to " location)})

(def default-handler
  (rr/routes
   (rr/create-default-handler
    {:not-found (constantly {:status 404, :body "404 Not Found"})})))

(def router
  (rr/router
   ["/" [["" {:name    ::root
              :handler (fn [req] (redirect "/publisher"))}]
         ["ping" {:name    ::ping
                  :handler pong}]
         ["api/v1/today.json" {:name    :kosa-crux.routes.api/today
                               :handler pali-word-handler/list}]

         ;; TODO: rails-ify / crud-ify / rest-ify resource routes
         ["publisher" [["" {:name    ::publisher
                            :handler kosa-crux.publisher.handler/index}]
                       ["/today" [["/pali_word_cards" {:name ::pali-word-index
                                                       :get  pali-word-handler/index}]
                                  ["/pali_word_card/new" {:name ::pali-word-new
                                                          :get  pali-word-handler/new}]
                                  ["/pali_word_card" {:name ::pali-word-create
                                                      :post (wrap-spec-validation :entity/pali-word-request pali-word-handler/create)}]
                                  ["/pali_word_card/:id" {:name ::pali-word-show
                                                          :get  pali-word-handler/show}]]]]]]]
   ;; CRUD resources conflict between /new and /:id
   ;; consider {:conflicting true} instead, once we abstract CRUDs
   {:conflicts nil}))

;; example crud-ful routes:

;; GET    /publisher/cards/pali_word_cards(.:format)                    cards/pali_word_cards#index
;; POST   /publisher/cards/pali_word_cards(.:format)                    cards/pali_word_cards#create
;; GET    /publisher/cards/pali_word_cards/new(.:format)                cards/pali_word_cards#new
;; GET    /publisher/cards/pali_word_cards/:id/edit(.:format)           cards/pali_word_cards#edit
;; GET    /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#show
;; PATCH  /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#update
;; PUT    /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#update
;; DELETE /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#destroy
