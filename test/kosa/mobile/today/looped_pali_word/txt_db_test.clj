(ns ^:database kosa.mobile.today.looped-pali-word.txt-db-test
  (:require [clojure.test :refer :all]
            [kosa.fixtures.model-fixtures :as model]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.mobile.today.looped-pali-word.txt :as sut]
            [kosa.mobile.today.looped-pali-word.db :as db]
            [kosa.mobile.today.looped.txt :as looped]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.support.debugging :refer :all]
            [kuti.support.time :as time]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(def i (sut/->PaliIngester))

(deftest ingesting-txt-file
  (testing "inserts entries into db"
    (let [f (file-fixtures/file "pali_word_raw.txt")]
      (sut/ingest f "eng")
      (is (= 3 (count (db/list)))))))

(deftest merging-new-entities
  (testing "ignore identical entities"
    (db/save! (model/looped-pali-word
               {:looped-pali-word/pali "suriya"
                :looped-pali-word/translations [["eng" "sun"]]
                :looped-pali-word/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "suriya"
                     :looped-pali-word/translations [["eng" "sun"]]
                     :looped-pali-word/published-at (time/parse "2012-01-01")}))
    (let [suriya (db/find-all :looped-pali-word/pali "suriya")]
      (is (= 1 (count  suriya)))
      (is (= (time/parse "2008-01-01")
             (-> suriya first :looped-pali-word/published-at)))))

  (testing "merge additional languages if merged is not identical"
    (db/save! (model/looped-pali-word
               {:looped-pali-word/pali "canda"
                :looped-pali-word/translations [["eng" "moon"]
                                                ["hin" "चंद"]]
                :looped-pali-word/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "canda"
                     :looped-pali-word/translations [["fra" "lune"]
                                                     ["spa" "luna"]]
                     :looped-pali-word/published-at (time/parse "2012-01-01")}))
    (let [canda (db/find-all :looped-pali-word/pali "canda")]
      (is (= 1 (count  canda)))
      (is (= [["eng" "moon"]
              ["hin" "चंद"]
              ["fra" "lune"]
              ["spa" "luna"]]
             (-> canda first :looped-pali-word/translations))))))

(deftest indexing
  (testing "index auto-increments"
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "tara"
                     :looped-pali-word/translations [["eng" "star"]]}))
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "kujagaha"
                     :looped-pali-word/translations [["eng" "mars"]]}))
    (let [tara (db/find-all :looped-pali-word/pali "tara")
          kujagaha (db/find-all :looped-pali-word/pali "kujagaha")]
      (is (= 1 (- (-> kujagaha first :looped-pali-word/index)
                  (-> tara first :looped-pali-word/index)))))))
