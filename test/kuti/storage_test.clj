(ns kuti.storage-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [mount.core :as mount]
            [kuti.support.debugging :refer :all]
            [kuti.fixtures.file-fixtures :as file-fixtures]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kuti.support]
            [kuti.support.time :as time]
            [kuti.record]
            [kuti.storage.core :as core]
            [kuti.storage :as sut]
            [kuti.fixtures.time-fixtures :as time-fixtures])
  (:import [java.io FileNotFoundException]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995)

(use-fixtures :each
  file-fixtures/with-fixture-files
  storage-fixtures/set-service-config)

(def params1 {:type :leaf-artefact
              :leaf-file {:filename "bodhi-with-raindrops.jpg",
                          :content-type "image/jpeg",
                          :tempfile (io/file "test/kuti/fixtures/files/bodhi-temp.jpg")
                          :size 13468}
              :submit "Save"})

(def ws-params {:type :leaf-artefact
                :leaf-file {:filename "bodhi with\twhitespace.jpg",
                            :content-type "image/jpeg",
                            :tempfile (io/file "test/kuti/fixtures/files/bodhi-temp.jpg")
                            :size 13468}
                :submit "Save"})

(deftest attachment
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing "returns an 'attachment' document"
      (is (= {:kuti/type :attm
              :attm/updated-at @time/clock
              :attm/key "a2e0d5505185beb708ac5edaf4fc4d20"
              :attm/filename "bodhi-with-raindrops.jpg"
              :attm/content-type "image/jpeg"
              :attm/metadata ""
              :attm/service-name :disk
              :attm/byte-size 13468
              :attm/checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
              :attm/identified true}
             attachment)))

    (testing "attachment's service filename identifies it as a kuti.storage file"
      (is (re-matches #"tmp/storage/kuti-.*-bodhi-with-raindrops\.jpg"
                      (sut/service-filename attachment))))

    (testing "url is prefixed with path from service config"
      (is (re-matches #"/uploads/kuti-.*-bodhi-with-raindrops\.jpg"
                      (sut/url attachment))))))

(deftest hash-key
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing ":attm/key is blake2b-encoded"
      (is (= "a2e0d5505185beb708ac5edaf4fc4d20" (:attm/key attachment))))))

(deftest unfurling
  (let [attachment (sut/params->attachment! (:leaf-file params1))]
    (testing "byte size is recorded"
      (is (= 13468 (:attm/byte-size attachment))))

    (testing "md5 checksum is recorded"
      (is (= "ca20bbfbea75755b1059ff2cd64bd6d3" (:attm/checksum attachment))))))

(deftest file-size
  (testing "attached file on disk has the same length as the uploaded file"
    (let [attachment (sut/params->attachment! (:leaf-file params1))
          local-file (sut/file attachment)]
      (is (= 13468 (.length local-file))))))

(deftest missing-root-directory
  (testing "throws an exception when file copy fails"
    (mount/stop #'core/service-config)
    (-> (mount/with-args {:storage {:service :disk
                                    :root    "this/directory/does/not/exist/"
                                    :path    "/uploads"}})
        (mount/only #{#'core/service-config})
        mount/start)
    (is (thrown? FileNotFoundException
                 (sut/params->attachment! (:leaf-file params1))))))

(deftest funky-characters
  (testing "replaces whitespace with underscores"
    (let [attachment (sut/params->attachment! (:leaf-file ws-params))]
      (testing "returns an 'attachment' document with underscores"
        (is (= {:kuti/type :attm
                :attm/updated-at @time/clock
                :attm/key "a2e0d5505185beb708ac5edaf4fc4d20"
                :attm/filename "bodhi_with_whitespace.jpg"
                :attm/content-type "image/jpeg"
                :attm/metadata ""
                :attm/service-name :disk
                :attm/byte-size 13468
                :attm/checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
                :attm/identified true}
               attachment))))))
