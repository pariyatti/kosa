(ns kosa.mobile.today.looped-pali-word.publish-job-test
  (:require [clojure.test :refer :all]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kosa.mobile.today.looped-pali-word.publish-job :as sut]
            [kosa.mobile.today.looped-pali-word.txt :as txt]
            [kosa.mobile.today.pali-word.db :as db]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest schedule
  (testing "running job publishes a new pali word card from looped template"
    (txt/db-insert! (model/looped-pali-word
                     {:looped-pali-word/pali "tara"
                      :looped-pali-word/translations [["en" "star"]]}))
    (sut/run-job! nil)
    (let [tara (db/q :pali-word/pali "tara")]
      (is (= 1 (count tara))))))
