(ns kosa.library.artefacts.image.handler
  (:refer-clojure :exclude [update])
  (:require [clojure.java.io :as io]
            [kuti.storage :as storage]
            [kuti.controller :as c]
            [kosa.library.artefacts.image.db :as db]
            [kosa.library.artefacts.image.views :as views]
            [kosa.views :as v]
            [ring.util.response :as resp])
  (:import [java.net URI]))

(defn ->image-doc [p]
  (-> p
      (c/params->doc [:xt/id])
      (assoc :image-artefact/original-url (URI. ""))
      (storage/attach! :image-artefact/image-attachment (:file p))))

(defn index [request]
  (let [images (db/list)]
    (resp/response
     (views/index request images))))

(defn new [request]
  (resp/response
   (views/new request)))

(defn create [request]
  (let [params (:params request)
        doc (->image-doc params)
        image (db/save! doc)]
    (if image
      (resp/redirect (v/show-path request :images image))
      (resp/response
       (str "It looks like your image wasn't saved? 'db/put' returned nil.")))))

(defn show [request]
  (let [image (db/get (-> request :path-params :id))]
    (if image
      (resp/response (views/show request image))
      (resp/response "Image artefact not found in database."))))

(defn edit [request]
  (let [image (db/get (-> request :path-params :id))]
    (if image
      (resp/response (views/edit request image))
      (resp/response "Image artefact not found in database."))))

(defn update [request]
  (let [params (:params request)
        doc (->image-doc params)
        image (db/save! doc)]
    (if image
      (resp/redirect (v/show-path request :images image))
      (resp/response
       (str "It looks like your image wasn't saved? 'db/put' returned nil.")))))

(defn destroy [request]
  (let [image (db/get (-> request :path-params :id))]
    (if image
      (do
        (db/delete image)
        (-> (resp/redirect (format "/library/artefacts/images"))
            (assoc :flash "Image Deleted.")))
      (resp/response "Image artefact not found in database."))))
