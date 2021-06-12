(ns kosa.middleware.auth
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication
                                           wrap-authorization]]
            ))

(defn- auth-fn [request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (if (= password "shakyamuni")
      username
      nil)))

(def- backend (backends/basic {:realm "kosa" :authfn auth-fn}))

(defn- basic-auth-middleware [handler]
  (fn [request]
    (if (authenticated? request)
      (handler request)
      (throw-unauthorized {:message "Not authorized"}))))

(def middleware-list
  [[wrap-authentication backend]
   [wrap-authorization backend]
   basic-auth-middleware])
