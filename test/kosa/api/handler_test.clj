(ns kosa.api.handler-test
  (:require [clojure.java.io :as io]
            [kosa.library.artefacts.image.db :as image]
            [kosa.api.handler :as sut]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kuti.fixtures.file-fixtures :as file-fixtures]
            [kuti.storage :as storage]
            [kuti.support.time :as time]
            [clojure.data]
            [clojure.test :refer :all]
            [kuti.fixtures.time-fixtures :as time-fixtures])
  (:import [java.net URI]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(use-fixtures :each
  file-fixtures/copy-fixture-files
  storage-fixtures/set-service-config)

(def image-attachment {:filename "bodhi-with-raindrops.jpg"
                       :content-type "image/jpeg"
                       :metadata ""
                       :service-name :disk
                       :byte-size 13468
                       :checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
                       :identified true})

(def image-artefact {:type :image-artefact
                     :image-artefact/published-at time-fixtures/win95
                     :image-artefact/original-url (URI. "")})

(deftest search-acceptance
  (let [file {:filename "bodhi-with-raindrops.jpg",
              :content-type "image/jpeg",
              :tempfile (io/file "test/kuti/fixtures/files/bodhi-temp.jpg")
              :size 13468}
        image-artefact2 (storage/attach! image-artefact :image-artefact/image-attachment file)
        _ (image/save! image-artefact2)]

    (testing "returns list of images"
      (let [resp (sut/search {:params {:q "bodhi"}})
            resp (assoc resp :body (vec (doall (:body resp))))
            resp (-> resp
                     (assoc-in  [:body 0 :crux.db/id] nil)
                     (assoc-in  [:body 0 :image-artefact/image-attachment :crux.db/id] nil))
            expected {:status 200,
                      :headers {},
                      :body [{:crux.db/id nil
                              :type :image-artefact,
                              :image-artefact/updated-at @time/clock,
                              :image-artefact/published-at @time/clock,
                              :image-artefact/original-url (URI. "")
                              :image-artefact/searchables "bodhi with raindrops jpg bodhi-with-raindrops.jpg",
                              :image-artefact/image-attachment {:crux.db/id nil
                                                                :updated-at @time/clock
                                                                :key "a2e0d5505185beb708ac5edaf4fc4d20",
                                                                :service-name :disk,
                                                                :filename "bodhi-with-raindrops.jpg",
                                                                :checksum "ca20bbfbea75755b1059ff2cd64bd6d3",
                                                                :url "/uploads/kuti-a2e0d5505185beb708ac5edaf4fc4d20-bodhi-with-raindrops.jpg",
                                                                :content-type "image/jpeg",
                                                                :identified true,
                                                                :metadata ""
                                                                :byte-size 13468}}]}]

        (is (= expected resp))))))
