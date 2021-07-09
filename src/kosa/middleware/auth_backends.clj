(ns kosa.middleware.auth-backends
  (:require [buddy.auth.protocols :as proto]))

(defn permissive
  [& [{:keys [realm authfn]}]]
  (reify
    proto/IAuthentication
    (-parse [_ request]
      (if (authfn request {:authdata :ignored})
        nil ;; weirdly, `nil` escapes the backend chain, so this is what we want
        :truthy))
    (-authenticate [_ request data]
      nil)))
