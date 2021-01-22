(ns kosa.routes
  (:refer-clojure :exclude [resources])
  (:require [kosa.library.artefacts.image.handler :as image-handler]
            kosa.library.artefacts.image.spec
            kosa.library.handler
            [kosa.middleware :refer [wrap-spec-validation]]
            [kosa.publisher.entity.pali-word.handler :as pali-word-handler]
            kosa.publisher.entity.pali-word.spec
            kosa.publisher.handler
            [kutis.dispatch :refer [resources]]
            [muuntaja.core :as m]
            [reitit.ring :as rr]
            [ring.util.response :as resp]))

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
         ["api/v1/today.json" {:name    :kosa.routes.api/today
                               :handler pali-word-handler/list}]

         ["library" [["" {:name    ::library-index
                          :handler kosa.library.handler/index}]
                     ["/artefacts/" (resources :images)]]]

         ["publisher" [["" {:name    ::publisher-index
                            :handler kosa.publisher.handler/index}]
                       ["/today/" (resources :pali-words)]]]]]
   ;; CRUD resources conflict between /new and /:id
   ;; consider {:conflicting true} instead
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

(defn print-routes []
  (clojure.pprint/pprint (reitit.core/routes router)))
