(ns kuti.storage.open-uri
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [kuti.support.strings :as strings]
            [ring.util.mime-type :refer [ext-mime-type]]
            [org.httpkit.client :as http])
  (:import [java.io File]
           [java.net URI]))

(declare download-uri!)

(defn retry-download-uri [uri retries]
  (if (< retries 5)
    (let [next-retry (inc retries)]
      (log/debug (format "#### Body was empty. Retrying %s / 5 ####" next-retry))
      (download-uri! uri :retries next-retry))
    (do
      (log/debug "#### Retries consumed. Giving up. ####")
      (throw (ex-info "Body was repeatedly empty." {:uri uri})))))

(defn copy-uri-stream [filename temp-file body]
  (with-open [in body
              out (io/output-stream temp-file)]
    (io/copy in out))
  (log/debug (format "Saved file size: %s" (.length temp-file)))
  {:tempfile temp-file
   :filename filename
   :content-type (ext-mime-type filename)
   :size (.length temp-file)})

(defn download-uri! [uri & {:keys [retries]
                            :or {retries 0}}]
  (let [filename (strings/file-name uri)
        ext (strings/file-extension uri)
        temp-file (File/createTempFile "kuti-download-" ext)
        resp (http/get (str uri) {:follow-redirects true
                                  :as :stream})
        _ (log/debug (format "Downloading '%s' to '%s'." (str uri) temp-file))
        body (:body @resp)]
    (if body
      (copy-uri-stream filename temp-file body)
      (retry-download-uri uri retries))))
