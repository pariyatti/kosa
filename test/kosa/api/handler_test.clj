(ns kosa.api.handler-test
  (:require [clojure.java.io :as io]
            [kosa.library.artefacts.image.db :as image]
            [kosa.mobile.today.doha.db :as doha]
            [kosa.api.handler :as sut]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kuti.fixtures.file-fixtures :as file-fixtures]
            [kuti.storage :as storage]
            [kuti.support.time :as time]
            [kuti.record :as db]
            [clojure.data]
            [clojure.test :refer :all]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kosa.routes :as routes]
            [kosa.fixtures.model-fixtures :as m])
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
                       :attm/key ""
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

(deftest ^:database search-acceptance
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
                     (assoc-in  [:body 0 :xt/id] nil)
                     (assoc-in  [:body 0 :image-artefact/image-attachment :xt/id] nil))
            expected {:status 200,
                      :headers {},
                      :body [{:xt/id nil
                              :kuti/type :image-artefact,
                              :image-artefact/updated-at @time/clock,
                              :image-artefact/published-at @time/clock,
                              :image-artefact/original-url (URI. "")
                              :image-artefact/searchables "bodhi with raindrops jpg bodhi-with-raindrops.jpg",
                              :image-artefact/image-attachment {:xt/id nil
                                                                :kuti/type :attm
                                                                :attm/updated-at @time/clock
                                                                :attm/key "a2e0d5505185beb708ac5edaf4fc4d20",
                                                                :attm/service-name :disk,
                                                                :attm/filename "bodhi-with-raindrops.jpg",
                                                                :attm/checksum "ca20bbfbea75755b1059ff2cd64bd6d3",
                                                                :attm/url "/uploads/kuti-a2e0d5505185beb708ac5edaf4fc4d20-bodhi-with-raindrops.jpg",
                                                                :attm/content-type "image/jpeg",
                                                                :attm/identified true,
                                                                :attm/metadata ""
                                                                :attm/byte-size 13468}}]}]

        (is (= expected resp))))))

(deftest ^:database today-list-acceptance
  (testing "returns collated cards, ordered by date"
    ;; TODO: can all of these move into factories?
    (db/save! (m/image-attachment))
    (db/save! (m/stacked-inspiration
               {:stacked-inspiration/published-at #time/instant "2021-03-21T00:00:00.000000Z"}))
    (db/save! (m/pali-word
               {:pali-word/published-at #time/instant "2021-04-22T00:00:00.000000Z"}))
    (db/save! (m/words-of-buddha-audio-attachment))
    (db/save! (m/words-of-buddha
               {:words-of-buddha/published-at #time/instant "2021-05-23T00:00:00.000000Z"}))
    (db/save! (m/doha-audio-attachment))
    (db/save! (m/doha
               {:doha/published-at #time/instant "2022-06-26T00:00:00.000000Z"}))

    (let [resp (sut/today {:server-name "localhost"
                           :server-port 443
                           :scheme :https
                           :reitit.core/router routes/router})
          resp (assoc resp :body
                      (mapv #(select-keys % [:type :published_at])
                            (:body resp)))
          expected {:status 200,
                    :headers {},
                    :body [{:type "doha", :published_at "2022-06-26T00:00:00.000Z"}
                           {:type "words_of_buddha", :published_at "2021-05-23T00:00:00.000Z"}
                           {:type "pali_word", :published_at "2021-04-22T00:00:00.000Z"}
                           {:type "stacked_inspiration", :published_at "2021-03-21T00:00:00.000Z"}]}]

      (println "in today acceptance")
      (is (= expected resp)))))
