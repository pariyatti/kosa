(ns kutis.storage
  (:require [clojure.java.io :as io]
            [kutis.support :refer [path-join]]
            [kutis.record]
            [kutis.search]))

(def attachment-fields #{:key :filename :content-type :metadata :service-name :byte-size :checksum})

(def service-config (atom {}))

(defn set-service-config! [conf]
  (reset! service-config conf))

(defn attached-filename [attachment]
  (format "kutis-%s-%s" (:key attachment) (:filename attachment)))

(defn service-filename [attachment]
  (path-join (:root @service-config)
             (attached-filename attachment)))

(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn calculate-key [tempfile]
  (let [bytes (file->bytes tempfile)
        ;; TODO: replace with blake2b
        h (hash bytes)]
    h))

(defn save-file! [tempfile attachment]
  (.renameTo tempfile (io/file (service-filename attachment))))

(defn params->attachment! [file-params]
  (let [tempfile (:tempfile file-params)
        k (calculate-key tempfile)
        attachment {:key k
                    :filename (:filename file-params)
                    :content-type (:content-type file-params)
                    :metadata     ""
                    :service-name (:service @service-config)
                    :byte-size    0 ;; TODO: track byte size
                    :checksum     "" ;; TODO: track checksum
                    }
        _ (save-file! tempfile attachment)]
    attachment))

(defn put-attachment! [attachment]
  (if-let [saved (kutis.record/put attachment attachment-fields)]
    saved ;; (:crux.db/id attachment)
    (throw (ex-info "Attachment not saved."))))

;;;;;;;;;;;;;;;;;;
;;  PUBLIC API  ;;
;;;;;;;;;;;;;;;;;;

(defn attach!
  "`attr` must be of the form `:<name>-attachment`"
  [doc attr file-params]
  (let [attachment (params->attachment! file-params)
        attachment-in-db (put-attachment! attachment)]
    (assoc doc attr attachment-in-db)))

(defn dehydrate-one [doc attr]
  (let [attr-id (-> attr name (str "-id") keyword)
        attachment (get doc attr)
        attachment-id (:crux.db/id attachment)]
    (-> doc
        (kutis.search/tag-searchables (:filename attachment))
        (dissoc attr)
        (assoc attr-id attachment-id))))

(defn dehydrate-all [doc]
  (let [att-keys (vec (filter (fn [k] (clojure.string/includes? (name k) "attachment")) (keys doc)))]
    (reduce dehydrate-one doc att-keys)))

(defn file [attachment]
  (io/file (service-filename attachment)))

(defn url [attachment]
  (path-join (:path @service-config)
             (attached-filename attachment)))
