(ns kosa.mobile.today.stacked-inspiration.handler
  (:refer-clojure :exclude [list update])
  (:require [kosa.mobile.today.stacked-inspiration.db :as db]
            [kosa.mobile.today.stacked-inspiration.views :as views]
            [kosa.views :as v]
            [kutis.controller :as c]
            [kutis.storage :as storage]
            [ring.util.response :as resp]))

(defn ->stacked-inspiration-doc [p]
  (let [attachment (storage/params->attachment! (:file p))]
    (-> (c/params->doc p [:card-type :text])
        (assoc :image-attachment attachment))))

(defn index [request]
  (let [cards (db/list)]
    (resp/response
     (views/index request cards))))

(defn new [request]
  (resp/response
   (views/new request)))

(defn create [req]
  (let [doc (-> req :params ->stacked-inspiration-doc)
        card (db/put doc)]
    (if card
      (resp/redirect (v/show-path req :stacked-inspirations card))
      (resp/response
       (str "It looks like your card wasn't saved? 'db/put' returned nil.")))))

(defn show [req]
  (let [card (-> req :path-params :id db/get)]
    (if card
      (resp/response (views/show req card))
      (resp/response "Card not found in database."))))

(defn list [_request]
  (resp/response
   (db/list)))

(defn edit [_req])
(defn update [_req])
(defn destroy [_req])
