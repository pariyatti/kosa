(ns kosa-crux.entity.pali-word.handler
  (:refer-clojure :exclude [list])
  (:require [ring.util.response :as resp]
            [kosa-crux.entity.pali-word.db :as pali-word-db]
            [kosa-crux.entity.pali-word.views :as views]))

(defn index [_request]
  (let [cards (pali-word-db/list)]
    (resp/response
     (views/index cards))))

(defn new [_request]
  (resp/response
   (views/new)))

(defn create [{:keys [params]}]
  (let [card (pali-word-db/put (assoc params :published-at (java.util.Date.)))]
    (resp/response
     (str "maybe your card was saved? " card))))

(defn list [_request]
  (resp/response
   (pali-word-db/list)))
