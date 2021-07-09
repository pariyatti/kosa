(ns kosa.middleware.auth
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends :as backends]
            [buddy.hashers :as hashers]
            [buddy.auth.middleware :refer [wrap-authentication
                                           wrap-authorization]]))

;; NOTE: `derive` uses a random salt by default and you will not
;;       get this same hash again if you recreate it from the password. -sd
(def avs-password-hash "bcrypt+sha512$84f97d76ed0d9d5fce692136778af80b$12$ac37445c969fedad3642d5e2c414aa86cb6160b5eac50a63")

(defn- auth-fn [request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (if (and
         (= "admin" username)
         (:valid (hashers/verify password
                                 avs-password-hash)))
      username
      nil)))

(def backend (backends/basic {:realm "kosa" :authfn auth-fn}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; public, reitit-style middleware wrappers:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn wrap-wrap-authentication [handler]
  (wrap-authentication handler backend))

(def authentication-middleware
  {:name ::authentication
   :wrap wrap-wrap-authentication})

(defn wrap-wrap-authorization [handler]
  (wrap-authorization handler backend))

(def authorization-middleware
  {:name ::authorization
   :wrap wrap-wrap-authorization})

(defn basic-auth-middleware [handler]
  (fn [request]
    (if (authenticated? request)
      (handler request)
      (throw-unauthorized {:message "Not authorized"}))))

(def basic-http-auth-middleware
  {:name ::basic-http-auth
   :wrap basic-auth-middleware})
