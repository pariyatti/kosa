(ns kosa.middleware.debugging
  [:require [kuti.support.debugging :refer [dbg]]])

(defn wrap-debug-request
  "Just some lazy debugging trash"
  [h]
  (fn [req]
    (dbg "REQUEST:"
         req)
    (h req)))
