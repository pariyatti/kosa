(ns kosa-crux.routes
  (:require [ring.util.response :as resp]
            [reitit.ring :as rr]
            [muuntaja.core :as m]
            [kosa-crux.publisher.handler]
            [kosa-crux.publisher.entity.pali-word.spec]
            [kosa-crux.library.artefacts.image.spec]
            [kosa-crux.middleware :refer [wrap-spec-validation]]
            [kosa-crux.library.artefacts.image.handler :as image-handler]
            [kosa-crux.publisher.entity.pali-word.handler :as pali-word-handler]))

(defn pong [_request]
  (resp/response "pong"))

(defn redirect [location]
  {:status  307
   :headers {"Location" location}
   :body    (str "Redirect to " location)})

(def default-handler
  (rr/routes
   (rr/create-resource-handler {:path "/css"     :root "public/css"})
   (rr/create-resource-handler {:path "/js"      :root "public/js"})
   (rr/create-resource-handler {:path "/images"  :root "public/images"})
   ;; TODO: move `uploads` _out_ of `/public`? -sd
   (rr/create-resource-handler {:path "/uploads" :root "public/uploads"})
   (rr/create-resource-handler {:path "/"        :root "public"})
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

         ["library" [["" {:name    ::library
                          :handler kosa-crux.library.handler/index}]
                     ;; TODO: crud-ify resource routes before adding anything after images
                     ["/artefacts" [["/images" {:name ::images-index
                                                :get  image-handler/index}]
                                    ["/image/new" {:name ::image-new
                                                   :get  image-handler/new}]
                                    ["/image" {:name ::image-create
                                               :post (wrap-spec-validation :entity/image-request image-handler/create)}]
                                    ["/image/:id" {:name ::image-show
                                                   :get  image-handler/show}]
                                    ;; TODO: edit
                                    ;; TODO: update
                                    ["/image/:id/delete" {:name ::image-destroy
                                                          ;; TODO: need to transform marked POST requests
                                                          ;;       at `/image/:id` into DELETE requests
                                                          ;;       instead of this named hack. -sd
                                                          :post image-handler/destroy}]]]]]

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
   {:conflicts nil
    :data {:muuntaja m/instance}}))

;; example crud-ful routes:

;; GET    /publisher/cards/pali_word_cards(.:format)                    cards/pali_word_cards#index
;; POST   /publisher/cards/pali_word_cards(.:format)                    cards/pali_word_cards#create
;; GET    /publisher/cards/pali_word_cards/new(.:format)                cards/pali_word_cards#new
;; GET    /publisher/cards/pali_word_cards/:id/edit(.:format)           cards/pali_word_cards#edit
;; GET    /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#show
;; PATCH  /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#update
;; PUT    /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#update
;; DELETE /publisher/cards/pali_word_cards/:id(.:format)                cards/pali_word_cards#destroy
