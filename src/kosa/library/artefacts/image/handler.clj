(ns kosa.library.artefacts.image.handler
  (:refer-clojure :exclude [update])
  (:require [clojure.java.io :as io]
            [kosa.library.artefacts.image.db :as db]
            [kosa.library.artefacts.image.views :as views]
            [kosa.views :as v]
            [ring.util.response :as resp]))

(defn index [request]
  (let [images (db/list)]
    (resp/response
     (views/index request images))))

(defn new [request]
  (resp/response
   (views/new request)))

(defn params->doc [p h]
  (-> p
      (assoc :hash h)
      ;; TODO: obvs temporary. get local file root from config --
      (assoc :url (format "/uploads/img%s" h))
      (assoc :filename (-> p :image-file :filename))
      (assoc :content-type (-> p :image-file :content-type))
      (dissoc :image-file)
      (assoc :published-at (java.util.Date.))))

(defn local-file-from [h]
  ;; TODO: local file root from config
  (io/file (format "/Users/steven/work/pariyatti/kosa/resources/public/uploads/img%s" h)))

(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn save-file! [p]
  (let [tmp-file (io/file (-> p :image-file :tempfile))
        bytes (file->bytes tmp-file)
        ;; bytes (-> p :image-file :tempfile slurp)
        ;; TODO: replace with blake2b
        h (hash bytes)]
    ;; (spit (local-file-from h) bytes)
    (.renameTo tmp-file (local-file-from h))
    h))

(defn create [request]
  (let [params (:params request)
        h (save-file! params)
        doc (params->doc params h)
        image (db/put doc)]
    (if image
      (resp/redirect (v/show-path request :images image))
      (resp/response
       (str "It looks like your image wasn't saved? 'crux/put' returned nil.")))))

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
  (let [params (assoc (:params request) :crux.db/id (-> request :path-params :id))
        h (save-file! params)
        doc (params->doc params h)
        image (db/put doc)]
    (if image
      (resp/redirect (v/show-path request :images image))
      (resp/response
       (str "It looks like your image wasn't saved? 'crux/put' returned nil.")))))

(defn destroy [{:keys [path-params]}]
  (let [image (db/get (:id path-params))]
    (if image
      (do
        (db/delete image)
        ;; TODO: add a flash
        (resp/redirect (format "/library/artefacts/images")))
      (resp/response "Image artefact not found in database."))))
