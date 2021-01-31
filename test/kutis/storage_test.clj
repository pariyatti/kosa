(ns kutis.storage-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [kutis.fixtures.record-fixtures :as record-fixtures]
            [kutis.fixtures.file-fixtures :as file-fixtures]
            [kutis.support]
            [kutis.record]
            [kutis.controller :as c]
            [kutis.storage :as sut]))

(use-fixtures :once record-fixtures/load-states)
(use-fixtures :each file-fixtures/copy-fixture-files)

(def params1 {:type "leaf_artefact",
              :leaf-file {:filename "bodhi-with-raindrops.jpg",
                          :content-type "image/jpeg",
                          :tempfile (io/file "test/kutis/fixtures/files/bodhi-temp.jpg")
                          :size 13468}
              :submit "Save"})

(sut/set-service-config! {:service :disk
                          :root    "resources/storage/"
                          :path    "/uploads"})

(deftest attachment
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing "returns an 'attachment' document"
      (is (= {;;:key 1643646293
              :filename "bodhi-with-raindrops.jpg"
              :content-type "image/jpeg"
              :metadata ""
              :service-name :disk
              :byte-size 13468
              :checksum ""}
             (dissoc attachment :key))))

    (testing "attachment's service filename identifies it as a kutis.storage file"
      (is (re-matches #"resources/storage/kutis-.*-bodhi-with-raindrops\.jpg"
                      (sut/service-filename attachment))))

    (testing "url is prefixed with path from service config"
      (is (re-matches #"/uploads/kutis-.*-bodhi-with-raindrops\.jpg"
                      (sut/url attachment))))))

(deftest file-size
  (testing "attached file on disk has the same length as the uploaded file"
    (let [attachment (sut/params->attachment! (:leaf-file params1))
          local-file (sut/file attachment)]
      (is (= 13468 (.length local-file))))))

(deftest byte-size
  (testing "byte size is recorded"
    (let [attachment (sut/params->attachment! (:leaf-file params1))]
      (is (= 13468 (:byte-size attachment))))))

(deftest attach!
  (let [doc1 (c/params->doc params1 [:type :leaf-file])
        doc2 (sut/attach! doc1 :leaf-attachment (:leaf-file doc1))]

    (testing "records the attachment to disk"
      (let [local-file (sut/file (:leaf-attachment doc2))]
        (is (= 13468 (.length local-file)))))

    (testing "records the attachment in Crux"
      (let [attachment (kutis.record/get (-> doc2 :leaf-attachment :crux.db/id))]
        (is (not (nil? (:crux.db/id attachment))))
        (is (= {;;:key 1643646293
                :filename "bodhi-with-raindrops.jpg"
                :content-type "image/jpeg"
                :metadata ""
                :service-name :disk
                :byte-size 13468
                :checksum ""}
               (dissoc attachment :key :crux.db/id)))))))

(deftest collapse
  (let [doc1 (c/params->doc params1 [:type])
        doc2 (sut/attach! doc1 :leaf-attachment (:leaf-file params1))
        leaf-attachment-id (-> doc2 :leaf-attachment :crux.db/id)
        doc3 (sut/collapse-all doc2)]

    (testing "collapses all attachments"
      (is (not (nil? (:leaf-attachment-id doc3))))
      (is (= {:type "leaf_artefact"
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
