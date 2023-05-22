(ns kosa.mobile.today.donation.handler
    (:refer-clojure :exclude [list update])
    (:require [kosa.mobile.today.donation.db :as donation-db]
              [kosa.mobile.today.donation.views :as views]
              [kosa.views :as v]
              [kuti.controller :as c]
              [kuti.support.debugging :refer :all]
              [ring.util.response :as resp])
    (:import [java.net URI]))

(def namespacer (partial c/namespaced :donation))

(defn ->donation-doc [p]
  (-> p
      namespacer
      (c/params->doc [:donation/header
                      :donation/title
                      :donation/text
                      :donation/button])))

(defn index [request]
  (let [cards (donation-db/list)]
    (resp/response
     (views/index request cards))))

(defn new [request]
  (resp/response
   (views/new request)))

(defn create [req]
  (let [doc (-> req :params ->donation-doc)
        card (donation-db/save! doc)]
    (if card
      (resp/redirect (v/show-path req :donations card))
      (resp/response
       (str "It looks like your card wasn't saved? 'db/put' returned nil.")))))

(defn show [req]
  (let [card (-> req :params :xt/id donation-db/get)]
    (if card
      (resp/response (views/show req card))
      (resp/not-found "Card not found in database."))))

(defn show-json [req]
  (let [card (-> req :params :xt/id donation-db/get)]
    (if card
      (resp/response card)
      (resp/not-found "Donation card not found."))))

(defn list [_request]
  (resp/response
   (donation-db/list)))

(defn edit [_req])
(defn update [_req])
(defn destroy [_req])
