(ns kosa-crux.routes
  (:refer-clojure :exclude [resources])
  (:require [ring.util.response :as resp]
            [reitit.ring :as rr]
            [muuntaja.core :as m]
            [cages.dispatch :refer [resources]]
            [kosa-crux.publisher.handler]
            [kosa-crux.publisher.entity.pali-word.spec]
            [kosa-crux.library.handler]
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
   (rr/routes
    (rr/redirect-trailing-slash-handler {:method :strip})
    (rr/create-default-handler
     {:not-found          (constantly {:status 404, :body "404: Not Found."})
      :method-not-allowed (constantly {:status 405, :body "405: Method Not Allowed."})
      :not-acceptable     (constantly {:status 406, :body "406: Not Acceptable."})}))))

(def router
  (rr/router
   ["/" [["" {:name    ::root
              :handler (fn [req] (redirect "/publisher"))}]
         ["ping" {:name    ::ping
                  :handler pong}]
         ["api/v1/today.json" {:name    :kosa-crux.routes.api/today
                               :handler pali-word-handler/list}]

         ["library" [["" {:name    ::library-index
                          :handler kosa-crux.library.handler/index}]
                     ["/artefacts/" (resources :images)]]]

         ["publisher" [["" {:name    ::publisher-index
                            :handler kosa-crux.publisher.handler/index}]
                       ["/today" [["/pali_word_cards" {:name ::pali-word-cards-index
                                                       :aliases [::pali-word-cards-create]
                                                       :get  pali-word-handler/index
                                                       :post (wrap-spec-validation :entity/pali-word-request pali-word-handler/create)}]
                                  ["/pali-word-cards/new" {:name ::pali-word-cards-new
                                                           :get  pali-word-handler/new}]
                                  ["/pali-word-cards/:id" {:name   ::pali-word-cards-show
                                                           :aliases [::pali-word-cards-update ::pali-word-cards-destroy]
                                                           :get    pali-word-handler/show
                                                           :put    pali-word-handler/update
                                                           :delete pali-word-handler/destroy}]
                                  ["/pali-word-cards/:id/edit" {:name ::pali-word-cards-edit
                                                                :get  pali-word-handler/edit}]]]]]]]
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
