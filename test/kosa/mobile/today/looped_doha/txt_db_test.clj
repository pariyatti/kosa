(ns ^:database kosa.mobile.today.looped-doha.txt-db-test
  (:require [kosa.mobile.today.looped-doha.txt :as sut]
            [kosa.mobile.today.looped-doha.db :as db]
            [kosa.mobile.today.looped.txt :as looped]
            [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kuti.support.time :as time]
            [kuti.support.debugging :refer :all])
  (:import [java.net URI]))

(use-fixtures :once
  storage-fixtures/set-service-config)

(use-fixtures :each
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(def i (sut/->DohaIngester))

(deftest ingesting-txt-file
  (testing "inserts entries into db"
    (let [f (file-fixtures/file "doha_raw.txt")]
      (sut/ingest f "eng")
      (is (= 2 (count (db/list)))))))

(deftest merging-entities
  (testing "ignore identical entities"
    (db/save! (model/looped-doha
               {:looped-doha/doha "Dukhī dekha karuṇā jage, "
                :looped-doha/translations [["eng" "Seeing the wretched, may compassion arise;"]]
                :looped-doha/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-doha
                          {:looped-doha/doha "Dukhī dekha karuṇā jage, "
                           :looped-doha/translations [["eng" "Seeing the wretched, may compassion arise;"]]
                           :looped-doha/published-at (time/parse "2012-01-01")}))
    (let [dukhi (db/find-all :looped-doha/doha "Dukhī dekha karuṇā jage, ")]
      (is (= 1 (count dukhi)))
      (is (= (time/parse "2008-01-01")
             (-> dukhi first :looped-doha/published-at)))))

  (testing "merge additional languages if merged is not identical"
    (db/save! (model/looped-doha
               {:looped-doha/doha "Barase barakhā samaya para, "
                :looped-doha/translations [["eng" "May the rains fall in due season, "]
                                           ["hin" "बारिशें बरखा समय पर,"]]
                :looped-doha/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-doha
                       {:looped-doha/doha "Barase barakhā samaya para, "
                        :looped-doha/translations [["lit" "Tegul lietus lyja laiku, "]
                                                   ["por" "Que as chuvas caiam na estação devida,"]]
                        :looped-doha/published-at (time/parse "2012-01-01")}))
    (let [barase (db/find-all :looped-doha/doha "Barase barakhā samaya para, ")]
      (is (= 1 (count  barase)))
      (is (= [["eng" "May the rains fall in due season, "]
              ["hin" "बारिशें बरखा समय पर,"]
              ["lit" "Tegul lietus lyja laiku, "]
              ["por" "Que as chuvas caiam na estação devida,"]]
             (-> barase first :looped-doha/translations))))))

(deftest indexing
  (testing "index auto-increments"
    (looped/db-insert! i (model/looped-doha
                       {:looped-doha/doha "Barase barakhā samaya para, "
                        :looped-doha/translations [["eng" "May the rains fall in due season, "]]}))
    (looped/db-insert! i (model/looped-doha
                       {:looped-doha/doha "Dukhī dekha karuṇā jage, "
                        :looped-doha/translations [["eng" "Seeing the wretched, may compassion arise;"]]}))
    (let [barase (db/find-all :looped-doha/doha "Barase barakhā samaya para, ")
          dukhi (db/find-all :looped-doha/doha "Dukhī dekha karuṇā jage, ")]
      (is (= 1 (- (-> dukhi first :looped-doha/index)
                  (-> barase first :looped-doha/index)))))))

(deftest indexing-edge-cases
  (testing "index does not increment if index already exists for card"
    (looped/db-insert! i (model/looped-doha
                       {:looped-doha/doha "Dukhī dekha karuṇā jage, "
                        :looped-doha/translations [["eng" "Seeing the wretched, may compassion arise;"]]}))
    (looped/db-insert! i (model/looped-doha
                       {:looped-doha/doha "Dukhī dekha karuṇā jage, "
                        :looped-doha/translations [["por" "Vendo o sofredor, que a compaixão surja;"]]}))
    (let [voca (db/find-all :looped-doha/doha "Dukhī dekha karuṇā jage, ")]
      (is (= 0 (-> voca first :looped-doha/index))))))

(deftest mp3s
  (testing "downloads and attaches mp3"
    (let [card (model/looped-doha
                {:looped-doha/audio-attachment
                 nil
                 :looped-doha/original-audio-url
                 (URI. "http://download.pariyatti.org/dohas/100_Doha.mp3")})
          e (looped/download-attachments! i "eng" card)]
      (is (= "100_Doha.mp3"
             (-> e :looped-doha/audio-attachment :attm/filename)))
      (is (= 424645
             (-> e :looped-doha/audio-attachment :attm/byte-size))))))
