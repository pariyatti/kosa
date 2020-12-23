(ns kutis.storage
  (:require [clojure.java.io :as io]
            [kutis.support :refer [path-join]]))

(def *blob-prefix* (atom ""))

(defn set-blob-prefix! [path]
  (reset! *blob-prefix* path))

(def blob-filename)

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

(defn local-file-from [attachment]
  (io/file (path-join @*blob-prefix*
                      (blob-filename attachment))))

(defn save-file! [tempfile attachment]
  (.renameTo tempfile (local-file-from attachment)))

(defn attach! [file-params]
  (let [tempfile (:tempfile file-params)
        k (calculate-key tempfile)
        attachment {:key k
                    :filename (:filename file-params)
                    :content-type (:content-type file-params)
                    :metadata     ""
                    :service-name :disk
                    :byte-size    0 ;; TODO: track byte size
                    :checksum     "" ;; TODO: track checksum
                    }
        _ (save-file! tempfile attachment)]
    attachment))

(defn blob-filename [attachment]
  (format "kutis-%s-%s" (:key attachment) (:filename attachment)))

(defn file [attachment]
  (io/file (local-file-from attachment)))

(defn url [attachment]
  (format "/uploads/img%s" (:key attachment)))
