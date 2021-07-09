(ns kosa.middleware.auth
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends :as backends]
            [buddy.hashers :as hashers]
            [buddy.auth.middleware :refer [wrap-authentication
                                           wrap-authorization]]
            [kosa.middleware.auth-backends :as kosa-backends]))

;; NOTE: `derive` uses a random salt by default and you will not
;;       get this same hash again if you recreate it from the password. -sd
;; TODO: We could collect this hash from the config to have separate
;;       passwords in prod and dev.
(def avs-password-hash "bcrypt+sha512$84f97d76ed0d9d5fce692136778af80b$12$ac37445c969fedad3642d5e2c414aa86cb6160b5eac50a63")

(defn always-allow?
  "This function is multi-arity only to maintain symmetry with the normal
   :authfn used with Buddy backends. Not strictly necessary, but probably
   less confusing in the future."
  ([{:keys [uri]}]
   (or (= "/" uri)
       (re-matches #"^/login.*" uri)
       (re-matches #"^/api.*" uri)
       (re-matches #"^/ping.*" uri)))
  ([req _authdata]
   (always-allow? req)))

(defn auth-fn [_request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (if (and
         (= "admin" username)
         (:valid (hashers/verify password
                                 avs-password-hash)))
      username
      nil)))

(def permissive-backend (kosa-backends/permissive {:realm "kosa" :authfn always-allow?}))
(def basic-backend (backends/basic {:realm "kosa" :authfn auth-fn}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; public, reitit-style middleware wrappers:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn wrap-wrap-authentication [handler]
  (wrap-authentication handler permissive-backend basic-backend))

(def authentication-middleware
  {:name ::authentication
   :wrap wrap-wrap-authentication})

(defn wrap-wrap-authorization [handler]
  (wrap-authorization handler basic-backend))

(def authorization-middleware
  {:name ::authorization
   :wrap wrap-wrap-authorization})

(defn basic-auth-middleware [handler]
  (fn [request]
    (if (or (always-allow? request)
            (authenticated? request))
      (handler request)
      (throw-unauthorized))))

(def basic-http-auth-middleware
  {:name ::basic-http-auth
   :wrap basic-auth-middleware})
