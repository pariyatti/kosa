(ns kutis.storage-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [kutis.support]
            [kutis.storage :as sut]))

(def params1 {:type "leaf_artefact",
              :leaf-file {:filename "bodhi-with-raindrops.jpg",
                          :content-type "image/jpeg",
                          :tempfile (io/file "test/kutis/fixtures/files/bodhi-temp.jpg")
                          :size 13468}
              :submit "Save"})

(defn copy-fixture-files [t]
  (io/copy "test/kutis/fixtures/files/bodhi.jpg" "test/kutis/fixtures/files/bodhi-temp.jpg")
  (t))

(use-fixtures :each copy-fixture-files)

(sut/set-blob-prefix! "resources/public/uploads")

(deftest attachment
  (let [attachment (sut/attach! (:leaf-file params1))]
    (testing "saves file to disk and returns an 'attachment' document"
      (is (= {;;:key 1643646293
              :filename "bodhi-with-raindrops.jpg"
              :content-type "image/jpeg"
              :metadata ""
              :service-name :disk
              :byte-size 0
              :checksum ""}
             (dissoc attachment :key))))

    (testing "attachment blob-filename identifies it as a kutis.storage file"
      (is (re-matches #"kutis-.*-bodhi-with-raindrops\.jpg" (sut/blob-filename attachment))))

    (testing "url uses blob prefix or something, then revisit image-handler"
      (is (= "uploads" (clojure.string/split (sut/url attachment)
                                             #"/"))))))

(deftest file-size
  (testing "attached file has the same length as the uploaded file"
    (let [attachment (sut/attach! (:leaf-file params1))
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


;; ***************************
;; Reference Articles
;; ***************************
;;
;; https://bloggie.io/@kinopyo/7-practical-tips-for-activestorage-on-rails-5-2
;; https://bibwild.wordpress.com/2018/10/03/some-notes-on-whats-going-on-in-activestorage/

;; Why bother doing this? To avoid too many files in one directory?
;; https://github.com/rails/rails/blob/7be33750d7e4c88d493c0e4c929eb66b8c40582d/activestorage/lib/active_storage/service/disk_service.rb#L149
