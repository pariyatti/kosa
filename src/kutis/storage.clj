(ns kutis.storage
  (:require [clojure.java.io :as io]
            [kutis.support :refer [path-join]]
            [kutis.record]
            [kutis.record.nested :as nested]
            [kutis.search]
            [kosa.config :as config]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :refer :all]
            [mount.core :as mount :refer [defstate]])
  (:import [java.io FileNotFoundException]))

(def attachment-fields #{:key :filename :metadata :service-name
                         :content-type :checksum :byte-size :identified})

(defn start-storage!
  ([]
   (:storage config/config))
  ([override]
   override))

(defstate service-config
  :start (start-storage!)
  :stop nil)

(defn set-service-config!
  "Only for test harnesses."
  [conf]
  (start-storage! conf))

(defn attached-filename [attachment]
  (format "kutis-%s-%s" (:key attachment) (:filename attachment)))

(defn service-dir []
  (:root service-config))

(defn service-filename [attachment]
  (let [dir (service-dir)]
    (if (.exists (io/file dir))
      (path-join dir (attached-filename attachment))
      (throw (FileNotFoundException. (str "Directory missing: " dir))))))

(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn calculate-key [tempfile]
  (-> (io/input-stream tempfile)
      (hash/blake2b-128)
      (bytes->hex)))

(defn calculate-md5 [tempfile]
  (-> (io/input-stream tempfile)
      (hash/md5)
      (bytes->hex)))

(defn save-file! [tempfile attachment]
  (.renameTo tempfile (io/file (service-filename attachment))))

(defn params->attachment! [file-params]
  (let [tempfile (:tempfile file-params)
        attachment {:key          (calculate-key tempfile)
                    :filename     (:filename file-params)
                    :metadata     ""
                    :service-name (:service service-config)
                    ;; unfurled:
                    :checksum     (calculate-md5 tempfile)
                    :content-type (:content-type file-params)
                    :byte-size    (.length tempfile)
                    :identified   true}]
    (if (save-file! tempfile attachment)
      attachment
      (throw (ex-info "Uploaded file failed to save to disk.")))))

(defn put-attachment! [attachment]
  (if-let [saved (kutis.record/put attachment attachment-fields)]
    saved ;; (:crux.db/id attachment)
    (throw (ex-info "Attachment failed to save to database."))))

;;;;;;;;;;;;;;;;;;
;;  PUBLIC API  ;;
;;;;;;;;;;;;;;;;;;

(defn attach!
  "`attr` must be of the form `:<name>-attachment`"
  [doc attr file-params]
  (let [attachment (params->attachment! file-params)
        attachment-in-db (put-attachment! attachment)]
    (assoc doc attr attachment-in-db)))

(defn reattach!
  "`attr` must be of the form `:<name>-attachment`
   `id` must be an existing attachment"
  [doc attr id]
  (let [attachment (kutis.record/get id)]
    (assoc doc attr attachment)))

(defn collapse-all [doc]
  (nested/collapse-all doc "attachment"))

(defn file [attachment]
  (io/file (service-filename attachment)))

(defn url [attachment]
  (path-join (:path service-config)
             (attached-filename attachment)))
