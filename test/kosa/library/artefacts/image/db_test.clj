(ns kosa.library.artefacts.image.db-test
  (:require [clojure.test :refer :all]
            [clojure.data]
            [clojure.java.io :as io]
            [dev.repl]
            [kosa.library.artefacts.image.db :as sut]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kuti.fixtures.file-fixtures :as file-fixtures]
            [kuti.record]
            [kuti.storage :as storage]
            [kuti.support.time :as time]
            [kuti.support.debugging :refer :all]
            [kuti.fixtures.time-fixtures :as time-fixtures])
  (:import [java.net URI]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(use-fixtures :each
  file-fixtures/with-fixture-files
  storage-fixtures/set-service-config)

(def image-attachment {:kuti/type :attm
                       :attm/updated-at time-fixtures/win95
                       :attm/key "a2e0d5505185beb708ac5edaf4fc4d20"
                       :attm/filename "bodhi-with-raindrops.jpg"
                       :attm/content-type "image/jpeg"
                       :attm/metadata ""
                       :attm/service-name :disk
                       :attm/byte-size 13468
                       :attm/checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
                       :attm/identified true})

(def image-artefact {:kuti/type :image-artefact
                     :image-artefact/published-at time-fixtures/win95
                     :image-artefact/original-url (URI. "")})

(defn clean-ids
  ([obj]
   (clean-ids obj nil))
  ([obj innards]
   (let [top-clean (dissoc obj
                           :crux.db/id
                           :image-artefact/image-attachment-id)]
     (if innards
       (update-in top-clean innards dissoc :crux.db/id)
       top-clean))))

(deftest attachment-acceptance-tests
  (let [file {:filename "bodhi-with-raindrops.jpg",
              :content-type "image/jpeg",
              :tempfile (io/file "test/kuti/fixtures/files/bodhi-temp.jpg")
              :size 13468}
        image-artefact2 (storage/attach! image-artefact
                                         :image-artefact/image-attachment
                                         file)]

    (testing "On insert, flattens Image Artefacts into (1) artefact and (2) attachment"
      (let [img (sut/save! image-artefact2)
            img-found (kuti.record/get (:crux.db/id img))
            attachment-found (kuti.record/get (:image-artefact/image-attachment-id img-found))]
        (is (= #{:crux.db/id
                 :kuti/type
                 :image-artefact/updated-at
                 :image-artefact/published-at
                 :image-artefact/original-url
                 :image-artefact/image-attachment-id
                 :image-artefact/searchables}
               (-> img-found keys set)))
        (is (= image-attachment (clean-ids attachment-found)))))

    (testing "On lookup, rehydrates attachment back into the artefact"
      (let [expected (assoc image-artefact
                            :image-artefact/updated-at time-fixtures/win95
                            :image-artefact/searchables "bodhi with raindrops jpg bodhi-with-raindrops.jpg"
                            :image-artefact/image-attachment
                            (assoc image-attachment
                                   :attm/url "/uploads/kuti-a2e0d5505185beb708ac5edaf4fc4d20-bodhi-with-raindrops.jpg"))
            img (sut/save! image-artefact2)
            img-found (sut/get (:crux.db/id img))
            img-no-ids (clean-ids img-found [:image-artefact/image-attachment])]
        (is (= expected img-no-ids))))

    (testing "On list, rehydrates all attachments"
      (let [expected (assoc image-attachment
                            :attm/url "/uploads/kuti-a2e0d5505185beb708ac5edaf4fc4d20-bodhi-with-raindrops.jpg")
            _ (sut/save! image-artefact2)
            _ (sut/save! image-artefact2)
            imgs (vec (sut/list))]
        (is (= expected (-> imgs first :image-artefact/image-attachment clean-ids)))
        (is (= expected (-> imgs second :image-artefact/image-attachment clean-ids)))))))
