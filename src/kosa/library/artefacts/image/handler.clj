(ns kosa.library.artefacts.image.handler
  (:refer-clojure :exclude [update])
  (:require [clojure.java.io :as io]
            [kutis.storage :as storage]
            [kutis.controller :as c]
            [kosa.library.artefacts.image.db :as db]
            [kosa.library.artefacts.image.views :as views]
            [kosa.views :as v]
            [ring.util.response :as resp]))

(defn ->image-doc [p]
  (let [attachment (storage/attach! (:image-file p))]
    (-> (c/params->doc p [:crux.db/id])
        (assoc :attached-image attachment))))

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
        image (db/put doc)]
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
  ;; TODO: extract the copying of :crux.db/id from params into
  ;;       doc into `controller.params->doc`.
  (let [params (assoc (:params request)
                      :crux.db/id (-> request :path-params :id))
        doc (->image-doc params)
        image (db/put doc)]
    (if image
      (resp/redirect (v/show-path request :images image))
      (resp/response
       (str "It looks like your image wasn't saved? 'db/put' returned nil.")))))

(defn destroy [{:keys [path-params]}]
  (let [image (db/get (:id path-params))]
    (if image
      (do
        ;; TODO: cascade record deletes to kutis.storage attachments, somehow?
        (db/delete image)
        ;; TODO: add a flash
        (resp/redirect (format "/library/artefacts/images")))
      (resp/response "Image artefact not found in database."))))
