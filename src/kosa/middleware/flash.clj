(ns kosa.middleware.flash
  (:require [ring.middleware.flash]
            [ring.middleware.session]
            [ring.middleware.session.cookie :as cookie]
            [kuti.support.bytes :as bytes]))

(def flash
  {:name ::flash
   :wrap ring.middleware.flash/wrap-flash})

(defn wrap-wrap-session [handler]
  (ring.middleware.session/wrap-session
   handler
   {:flash true
    :store (cookie/cookie-store {:key (bytes/str->byte-array "abcdabcdabcdabcd")})
    :cookie-attrs {:http-only true, :same-site :strict}}))

(def session
  {:name ::session
   :wrap wrap-wrap-session})
