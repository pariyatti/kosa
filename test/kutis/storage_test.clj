(ns kutis.storage-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [mount.core :as mount]
            [kutis.fixtures.record-fixtures :as record-fixtures]
            [kutis.fixtures.file-fixtures :as file-fixtures]
            [kutis.fixtures.storage-fixtures :as storage-fixtures]
            [kutis.support]
            [kutis.record]
            [kutis.controller :as c]
            [kutis.storage :as sut])
  (:import [java.io FileNotFoundException]))

(use-fixtures :once record-fixtures/load-states)
(use-fixtures :each
  file-fixtures/copy-fixture-files
  storage-fixtures/set-service-config)

(def params1 {:type "leaf-artefact",
              :leaf-file {:filename "bodhi-with-raindrops.jpg",
                          :content-type "image/jpeg",
                          :tempfile (io/file "test/kutis/fixtures/files/bodhi-temp.jpg")
                          :size 13468}
              :submit "Save"})

(def ws-params {:type "leaf-artefact",
                :leaf-file {:filename "bodhi with\twhitespace.jpg",
                            :content-type "image/jpeg",
                            :tempfile (io/file "test/kutis/fixtures/files/bodhi-temp.jpg")
                            :size 13468}
                :submit "Save"})

(deftest attachment
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing "returns an 'attachment' document"
      (is (= {:key "a2e0d5505185beb708ac5edaf4fc4d20"
              :filename "bodhi-with-raindrops.jpg"
              :content-type "image/jpeg"
              :metadata ""
              :service-name :disk
              :byte-size 13468
              :checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
              :identified true}
             attachment)))

    (testing "attachment's service filename identifies it as a kutis.storage file"
      (is (re-matches #"tmp/storage/kutis-.*-bodhi-with-raindrops\.jpg"
                      (sut/service-filename attachment))))

    (testing "url is prefixed with path from service config"
      (is (re-matches #"/uploads/kutis-.*-bodhi-with-raindrops\.jpg"
                      (sut/url attachment))))))

(deftest hash-key
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing ":key is blake2b-encoded"
      (is (= "a2e0d5505185beb708ac5edaf4fc4d20" (:key attachment))))))

(deftest unfurling
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing "byte size is recorded"
      (is (= 13468 (:byte-size attachment))))

    (testing "md5 checksum is recorded"
      (is (= "ca20bbfbea75755b1059ff2cd64bd6d3" (:checksum attachment))))))

(deftest file-size
  (testing "attached file on disk has the same length as the uploaded file"
    (let [attachment (sut/params->attachment! (:leaf-file params1))
          local-file (sut/file attachment)]
      (is (= 13468 (.length local-file))))))

(deftest missing-root-directory
  (testing "throws an exception when file copy fails"
    (mount/stop #'sut/service-config)
    (-> (mount/with-args {:storage {:service :disk
                                    :root    "this/directory/does/not/exist/"
                                    :path    "/uploads"}})
        (mount/only #{#'sut/service-config})
        mount/start)
    (is (thrown? java.io.FileNotFoundException
                 (sut/params->attachment! (:leaf-file params1))))))

(deftest funky-characters
  (testing "replaces whitespace with underscores"
    (let [attachment (sut/params->attachment! (:leaf-file ws-params))]
      (testing "returns an 'attachment' document with underscores"
        (is (= {:key "a2e0d5505185beb708ac5edaf4fc4d20"
                :filename "bodhi_with_whitespace.jpg"
                :content-type "image/jpeg"
                :metadata ""
                :service-name :disk
                :byte-size 13468
                :checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
                :identified true}
               attachment))))))

(deftest attach!
  (let [doc1 (c/params->doc params1 [:type :leaf-file])
        doc2 (sut/attach! doc1 :leaf-attachment (:leaf-file doc1))]

    (testing "records the attachment to disk"
      (let [local-file (sut/file (:leaf-attachment doc2))]
        (is (= 13468 (.length local-file)))))

    (testing "records the attachment in Crux"
      (let [attachment (kutis.record/get (-> doc2 :leaf-attachment :crux.db/id))]
        (is (not (nil? (:crux.db/id attachment))))
        (is (= {:key "a2e0d5505185beb708ac5edaf4fc4d20"
                :filename "bodhi-with-raindrops.jpg"
                :content-type "image/jpeg"
                :metadata ""
                :service-name :disk
                :byte-size 13468
                :checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
                :identified true}
               (dissoc attachment :crux.db/id)))))))

(deftest collapse
  (let [doc1 (c/params->doc params1 [:type])
        doc2 (sut/attach! doc1 :leaf-attachment (:leaf-file params1))
        leaf-attachment-id (-> doc2 :leaf-attachment :crux.db/id)
        doc3 (sut/collapse-all doc2)]

    (testing "collapses all attachments"
      (is (not (nil? (:leaf-attachment-id doc3))))
      (is (= {:type "leaf-artefact"
              :leaf-attachment-id leaf-attachment-id}
             (dissoc doc3 :published-at))))))

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

;; Service::DiskService - https://github.com/rails/rails/blob/5cfd58bbfb8425ab1931c618d98b649bab059ce6/activestorage/lib/active_storage/service/disk_service.rb
;; Why bother doing this? To avoid too many files in one directory?
;; https://github.com/rails/rails/blob/7be33750d7e4c88d493c0e4c929eb66b8c40582d/activestorage/lib/active_storage/service/disk_service.rb#L149
