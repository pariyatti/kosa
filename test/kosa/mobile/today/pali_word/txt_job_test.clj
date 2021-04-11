(ns kosa.mobile.today.pali-word.txt-job-test
  (:require [kosa.mobile.today.pali-word.txt-job :as sut]
            [kosa.mobile.today.pali-word.db :as db]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [clojure.test :refer :all]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest parsing-txt-file
  (testing "parses out text without whitespace or separators"
    (let [f (file-fixtures/file "pali_word_raw.txt")
          txt (slurp f)]
      (is (= [{:pali-word/pali "vimutti"
               :pali-word/translations [["en" "freedom, release, deliverance, emancipation, liberation"]]}
              {:pali-word/pali "kataññū"
               :pali-word/translations [["en" "kata + ññū = what is done + knowing, acknowledging what has been done (to, for one), grateful"]]}
              {:pali-word/pali "tarati"
               :pali-word/translations [["en" "to cross [a river], to surmount, overcome [the great flood of life, desire, ignorance], to get to the other side, to cross over, as in crossing the ocean of suffering"]]}]
             (sut/parse txt "en"))))))

(deftest ingesting-txt-file
  (testing "inserts entries into db"
    (let [f (file-fixtures/file "pali_word_raw.txt")]
      (sut/ingest f "en")
      (is (= 3 (count (db/list))))))

  (testing "TODO: ignore identical entities")

  (testing "TODO: merge additional languages if merged is not identical")

  (testing "TODO: figure out scheduling (override publish on `save!`?)"))
