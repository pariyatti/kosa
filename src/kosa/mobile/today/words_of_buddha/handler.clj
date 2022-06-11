(ns kosa.mobile.today.words-of-buddha.handler
  (:refer-clojure :exclude [list update])
  (:require [kosa.mobile.today.words-of-buddha.db :as db]
            [ring.util.response :as resp]))

(defn show-json [req]
  (let [card (-> req :params :xt/id db/get)]
    (if card
      (resp/response card)
      (resp/not-found "Words of Buddha card not found."))))
