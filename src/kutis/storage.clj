(ns kutis.storage
  (:require [clojure.java.io :as io]
            [kutis.support :refer [path-join]]))

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

(defn attach! [file-params]
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

(defn file [attachment]
  (io/file (service-filename attachment)))

(defn url [attachment]
  (path-join (:path @service-config)
             (attached-filename attachment)))
