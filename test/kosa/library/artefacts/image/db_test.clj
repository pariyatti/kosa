(ns kosa.library.artefacts.image.db-test
  (:require [clojure.test :refer :all]
            [kosa.library.artefacts.image.db :as sut]
            [kutis.fixtures.record-fixtures :as fixtures]
            [kutis.record]))

(use-fixtures :once fixtures/load-states)

(def image-attachment {:filename "bodhi-with-raindrops.jpg"
                       :content-type "image/jpeg"
                       :metadata ""
                       :service-name :disk
                       :byte-size 0
                       :checksum ""})

(def image-artefact {:type "image_artefact"
                     :image-attachment image-attachment})

(defn clean-ids
  ([obj]
   (clean-ids obj nil))
  ([obj innards]
   (let [top-clean (dissoc obj :crux.db/id :modified-at :image-attachment-id)]
     (if innards
       (update-in top-clean innards dissoc :crux.db/id)
       top-clean))))

(deftest db-operations
  (testing "On insert, flattens Image Artefacts into (1) artefact and (2) attachment"
    (let [img (sut/put image-artefact)
          img-found (kutis.record/get (:crux.db/id img))
          attachment-found (kutis.record/get (:image-attachment-id img-found))]
      (is (= #{:crux.db/id :modified-at :type :image-attachment-id :searchables}
             (-> img-found keys set)))
      (is (= image-attachment (clean-ids attachment-found)))))

  (testing "On lookup, rehydrates attachment back into the artefact"
    (let [img (sut/put image-artefact)
          img-found (sut/get (:crux.db/id img))
          img-no-ids (clean-ids img-found [:image-attachment])]
      (is (= (assoc image-artefact :searchables "bodhi with raindrops jpg bodhi-with-raindrops.jpg")
             img-no-ids))))

  (testing "On list, rehydrates all attachments"
    (let [img (sut/put image-artefact)
          img2 (sut/put image-artefact)
          imgs (vec (sut/list))]
      (is (= image-attachment (-> imgs first :image-attachment clean-ids)))
      (is (= image-attachment (-> imgs second :image-attachment clean-ids))))))
