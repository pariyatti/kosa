(ns kosa.library.artefacts.image.db-test
  (:require [clojure.test :refer :all]
            [clojure.data]
            [clojure.java.io :as io]
            [kosa.library.artefacts.image.db :as sut]
            [kutis.fixtures.record-fixtures :as record-fixtures]
            [kutis.fixtures.file-fixtures :as file-fixtures]
            [kutis.record]
            [kutis.storage :as storage]))

(use-fixtures :once record-fixtures/load-states)
(use-fixtures :each file-fixtures/copy-fixture-files)

(def image-attachment {:filename "bodhi-with-raindrops.jpg"
                       :content-type "image/jpeg"
                       :metadata ""
                       :service-name :disk
                       :byte-size 13468
                       :checksum "ca20bbfbea75755b1059ff2cd64bd6d3"
                       :identified true})

(def image-artefact {:type "image_artefact"})

(storage/set-service-config! {:service :disk
                              :root    "resources/storage/"
                              :path    "/uploads"})

(defn clean-ids
  ([obj]
   (clean-ids obj nil))
  ([obj innards]
   (let [top-clean (dissoc obj :crux.db/id :key :modified-at :image-attachment-id)]
     (if innards
       (update-in top-clean innards dissoc :crux.db/id :key)
       top-clean))))

(deftest attachment-acceptance-tests
  (let [file {:filename "bodhi-with-raindrops.jpg",
              :content-type "image/jpeg",
              :tempfile (io/file "test/kutis/fixtures/files/bodhi-temp.jpg")
              :size 13468}
        image-artefact2 (storage/attach! image-artefact :image-attachment file)]

    (testing "On insert, flattens Image Artefacts into (1) artefact and (2) attachment"
      (let [img (sut/put image-artefact2)
            img-found (kutis.record/get (:crux.db/id img))
            attachment-found (kutis.record/get (:image-attachment-id img-found))]
        (is (= #{:crux.db/id :modified-at :type :image-attachment-id :searchables}
               (-> img-found keys set)))
        (is (= image-attachment (clean-ids attachment-found)))))

    (testing "On lookup, rehydrates attachment back into the artefact"
      (let [expected (assoc image-artefact
                            :searchables "bodhi with raindrops jpg bodhi-with-raindrops.jpg"
                            :image-attachment image-attachment)
            img (sut/put image-artefact2)
            img-found (sut/get (:crux.db/id img))
            img-no-ids (clean-ids img-found [:image-attachment])]
        (prn (clojure.data/diff expected img-no-ids))
        (is (= expected img-no-ids))))

    (testing "On list, rehydrates all attachments"
      (let [img (sut/put image-artefact2)
            img2 (sut/put image-artefact2)
            imgs (vec (sut/list))]
        (is (= image-attachment (-> imgs first :image-attachment clean-ids)))
        (is (= image-attachment (-> imgs second :image-attachment clean-ids)))))))
