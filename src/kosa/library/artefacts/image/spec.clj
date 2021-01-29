(ns kosa.library.artefacts.image.spec
  (:require [clojure.string]
            [clojure.spec.alpha :as s]))

(defn file? [obj]
  (instance? java.io.File obj))

(s/def :file/filename (s/and string? #(-> % clojure.string/blank? not)))
(s/def :file/content-type (s/and string? #(-> % clojure.string/blank? not)))
(s/def :file/tempfile file?)
(s/def :file/size int?)

;; TODO: is `entity` the right keyword ns for this? (it can't be top-level)
(s/def :entity/file
  (s/keys :req-un [:file/filename
                   :file/content-type
                   :file/tempfile
                   :file/size]))

(defn multipart-file? [obj]
  (s/conform :entity/file obj))

(s/def :image/file multipart-file?)

(s/def :image/url (s/and string? #(-> % clojure.string/blank? not)))
(s/def :image/original-url (s/and string? #(-> % clojure.string/blank? not)))

(s/def :entity/image-request
  (s/keys :req-un [:image/file]
          :opt-un [:image/original-url]))
