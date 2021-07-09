(ns kosa.middleware.auth-backends
  (:require [buddy.auth.protocols :as proto]
            [kuti.support.debugging :refer [dbg]]))

(defn permissive
  [& [{:keys [realm authfn]}]]
  (reify
    proto/IAuthentication
    (-parse [_ request]
      (dbg "#### parsing")
      (dbg "always allow? (permissive)" (authfn request {:authdata :ignored}))
      (if (authfn request {:authdata :ignored})
        nil ;; weirdly, `nil` escapes the backend chain, so this is what we want
        :truthy))
    (-authenticate [_ request data]
      (dbg "authenticate permissive, return nil")
      nil)))
