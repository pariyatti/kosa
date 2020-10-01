(ns kosa-crux.publisher.entity.pali-word.handler
  (:refer-clojure :exclude [list])
  (:require [ring.util.response :as resp]
            [kosa-crux.publisher.entity.pali-word.db :as pali-word-db]
            [kosa-crux.publisher.entity.pali-word.views :as views]))

(defn index [_request]
  (let [cards (pali-word-db/list)]
    (resp/response
     (views/index cards))))

(defn new [_request]
  (resp/response
   (views/new)))

(defn create [{:keys [params]}]
  (let [card (pali-word-db/sync-put (assoc params :published-at (java.util.Date.)))]
    (if card
      (resp/redirect (format "/publisher/today/pali_word_card/%s" (:crux.db/id card)))
      (resp/response
       (str "It looks like your card wasn't saved? 'crux/sync-put' returned nil.")))))

(defn show [{:keys [route-params]}]
  (let [card (pali-word-db/get (:id route-params))]
    (if card
      (resp/response (views/show card))
      (resp/response "Card not found in Crux."))))

(defn list [_request]
  (resp/response
   (pali-word-db/list)))
