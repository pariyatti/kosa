(ns kutis.storage-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [kutis.storage :as sut]))

(deftest attach-from-parameters
  (testing "saves file to disk and returns an 'attachment' document"
    (let [params {:type "leaf_artefact",
                  :leaf-file {:filename "bodhi-with-raindrops.jpg",
                              :content-type "image/jpeg",
                              :tempfile (io/file "test/kutis/fixtures/files/bodhi.jpg")
                              :size 13468}
                  :submit "Save"}
          attachment (sut/attach! (:leaf-file params))
          local-file (sut/file attachment)]
      (is (= 13468 (.length local-file))))))


;; ***************************
;; ActiveStorage Blob Columns:
;; ***************************
;;
;; t.string   :key,          null: false
;; t.string   :filename,     null: false
;; t.string   :content_type
;; t.text     :metadata
;; t.string   :service_name, null: false
;; t.bigint   :byte_size,    null: false
;; t.string   :checksum,     null: false
;; t.datetime :created_at,   null: false
