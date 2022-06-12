(ns kosa.mobile.today.doha.handler
  (:refer-clojure :exclude [list update])
  (:require [kosa.mobile.today.doha.db :as db]
            [ring.util.response :as resp]))

(defn show-json [req]
  (let [card (-> req :params :xt/id db/get)]
    (if card
      (resp/response card)
      (resp/not-found "Doha card not found."))))
