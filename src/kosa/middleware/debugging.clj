(ns kosa.middleware.debugging)

(defn wrap-println-request
  "Just some lazy debugging trash"
  [h]
  (fn [req]
    (prn "REQUEST:")
    (prn req)
    (h req)))
