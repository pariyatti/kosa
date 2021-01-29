(ns kosa.routes
  (:refer-clojure :exclude [resources])
  (:require [kosa.library.handler]
            [kosa.library.artefacts.image.handler :as image-handler]
            [kosa.library.artefacts.image.spec]
            [kosa.mobile.handler]
            [kosa.mobile.today.pali-word.handler :as pali-word-handler]
            [kosa.mobile.today.pali-word.spec]
            [kosa.mobile.today.stacked-inspiration.handler :as stacked-inspiration-handler]
            [kosa.mobile.today.stacked-inspiration.spec]
            [kosa.search.handler :as search-handler]
            [kutis.dispatch :refer [resources]]
            [reitit.ring :as rr]
            [ring.util.response :as resp]

            ;; middleware nonsense:
            [kosa.middleware :refer [wrap-spec-validation]]
            [muuntaja.core :as m]
            [reitit.coercion.spec :as c]
            [reitit.ring.middleware.dev]
            [kosa.middleware.json :as json]
            [kosa.middleware.logger :as logger]
            [kosa.middleware.params :as params]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.multipart :as multipart]))

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
              :handler (fn [req] (redirect "/mobile"))}]
         ["ping" {:name    ::ping
                  :handler pong}]
         ["api/v1/today.json" {:name    :kosa.routes.api/today
                               :handler pali-word-handler/list}]
         ["api/v1/search" {:name    :kosa.routes.api/search
                           :handler search-handler/search}]

         ["library" [["" {:name    ::library-index
                          :handler kosa.library.handler/index}]
                     ["/artefacts/" (resources :images)]]]

         ["mobile" [["" {:name    ::mobile-index
                         :handler kosa.mobile.handler/index}]
                    ["/today/" (resources :pali-words :stacked-inspirations)]]]]]

   ;; CRUD resources conflict between /new and /:id
   ;; consider {:conflicting true} instead
   {:conflicts nil
    ;; WARNING: these diffs are very handy, but very slow:
    ;; :reitit.middleware/transform reitit.ring.middleware.dev/print-request-diffs
    :data {:muuntaja m/instance
           :coercion c/coercion
           :middleware
           [logger/log-request-start-middleware
            json/json-response
            json/json-params
            json/json-body
            parameters/parameters-middleware
            kosa.middleware.params/multipart-params
            muuntaja/format-negotiate-middleware
            exception/exception-middleware
            coercion/coerce-request-middleware
            params/keyword-params
            kosa.middleware/path-params
            logger/log-request-params-middleware
            logger/log-response-middleware]}}))
