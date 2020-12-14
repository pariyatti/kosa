(ns kosa-crux.library.artefacts.image.handler
  (:require [clojure.java.io :as io]
            [ring.util.response :as resp]
            [kosa-crux.library.artefacts.image.db :as db]
            [kosa-crux.library.artefacts.image.views :as views]))

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

(def ^:dynamic *params*)

(defn create [{:keys [params]}]
  (def ^:dynamic *params* params)
  (let [h (save-file! params)
        doc (params->doc params h)
        image (db/sync-put doc)]
    (if image
      (resp/redirect (format "/library/artefacts/image/%s" (:crux.db/id image)))
      (resp/response
       (str "It looks like your image wasn't saved? 'crux/sync-put' returned nil.")))))

(defn show [{:keys [path-params]}]
  (let [image (db/get (:id path-params))]
    (if image
      (resp/response (views/show image))
      (resp/response "Image artefact not found in database."))))
