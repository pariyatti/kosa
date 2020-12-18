(ns kosa-crux.publisher.entity.pali-word.handler
  (:refer-clojure :exclude [list])
  (:require [ring.util.response :as resp]
            [kosa-crux.views :as v]
            [kosa-crux.publisher.entity.pali-word.db :as pali-word-db]
            [kosa-crux.publisher.entity.pali-word.views :as views]))

(defn index [request]
  (let [cards (pali-word-db/list)]
    (resp/response
     (views/index request cards))))

(defn new [request]
  (resp/response
   (views/new request)))

(defn params->doc [p]
  (-> p
      (assoc :translations (map vector (:language p) (:translation p)))
      (dissoc :language :translation)
      (assoc :published-at (java.util.Date.))))

(defn create [req]
  (let [doc (-> req :params params->doc)
        card (pali-word-db/put doc)]
    (if card
      (resp/redirect (v/show-path req :pali-word-cards card))
      (resp/response
       (str "It looks like your card wasn't saved? 'crux/put' returned nil.")))))

(defn show [req]
  (let [card (-> req :path-params :id pali-word-db/get)]
    (if card
      (resp/response (views/show req card))
      (resp/response "Card not found in Crux."))))

(defn list [_request]
  (resp/response
   (pali-word-db/list)))

(defn edit [_req])
(defn update [_req])
(defn destroy [_req])
