(ns kosa.mobile.today.pali-word.handler
  (:refer-clojure :exclude [list update])
  (:require [kosa.mobile.today.pali-word.db :as pali-word-db]
            [kosa.mobile.today.pali-word.views :as views]
            [kosa.views :as v]
            [kuti.controller :as c]
            [kuti.support.debugging :refer :all]
            [ring.util.response :as resp])
  (:import [java.net URI]))

(def namespacer (partial c/namespaced :pali-word))

(defn ->pali-word-doc [p]
  (-> p
      namespacer
      (c/params->doc [:pali-word/bookmarkable
                      :pali-word/shareable
                      :pali-word/pali
                      [:pali-word/translations
                       #(map vector
                             (:pali-word/language %)
                             (:pali-word/translation %))]])
      (assoc :pali-word/original-pali "")
      (assoc :pali-word/original-url (URI. ""))))

(defn index [request]
  (let [cards (pali-word-db/list)]
    (resp/response
     (views/index request cards))))

(defn new [request]
  (resp/response
   (views/new request)))

(defn create [req]
  (let [doc (-> req :params ->pali-word-doc)
        card (pali-word-db/save! doc)]
    (if card
      (resp/redirect (v/show-path req :pali-words card))
      (resp/response
       (str "It looks like your card wasn't saved? 'db/put' returned nil.")))))

(defn show [req]
  (let [card (-> req :path-params :id pali-word-db/get)]
    (if card
      (resp/response (views/show req card))
      (resp/response "Card not found in database."))))

(defn list [_request]
  (resp/response
   (pali-word-db/list)))

(defn edit [_req])
(defn update [_req])
(defn destroy [_req])
