(ns kutis.storage
  (:require [clojure.java.io :as io]))

(defn local-file-from [h]
  ;; TODO: local file root from config
  (io/file (format "/Users/steven/work/pariyatti/kosa/resources/public/uploads/img%s" h)))

(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn save-file! [p]
  (def *params p)
  (let [tmp-file (io/file (-> p :tempfile))
        bytes (file->bytes tmp-file)
        ;; bytes (-> p :image-file :tempfile slurp)
        ;; TODO: replace with blake2b
        h (hash bytes)]
    ;; (spit (local-file-from h) bytes)
    (.renameTo tmp-file (local-file-from h))
    h))

(defn attach! [file-params]
  (let [h (save-file! file-params)]
    {:key h
     :filename (:filename file-params)
     :content-type (:content-type file-params)
     :metadata     ""
     :service-name :disk
     :byte-size    0 ;; TODO: track byte size
     :checksum     "" ;; TODO: track checksum
     }))

(defn file [attachment]
  (io/file (local-file-from (:key attachment))))

(defn url [attachment]
  (format "/uploads/img%s" (:key attachment)))
