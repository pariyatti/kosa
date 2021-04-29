(ns kosa.mobile.today.looped-pali-word.publish-job-test
  (:require [clojure.test :refer :all]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kosa.mobile.today.looped-pali-word.publish-job :as sut]
            [kosa.mobile.today.looped-pali-word.txt :as txt]
            [kosa.mobile.today.looped-pali-word.db :as loop-db]
            [kosa.mobile.today.pali-word.db :as pali-db]
            [kuti.support.time :as time]))

(use-fixtures :each
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest schedule
  (testing "publishes a new pali word card from looped template"
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "tara"
                     :looped-pali-word/translations [["en" "star"]]}))
    (sut/run-job! nil)
    (let [tara (pali-db/q :pali-word/pali "tara")]
      (is (= 1 (count tara))))))

(deftest schedule-ignores-an-empty-collection-of-looped-cards
  (testing "nothing happens (including no errors)"
    (is (nil? (sut/run-job! nil)))))

(deftest scheduling-against-epoch
  (testing "publishes the Nth card from the 'perl epoch' on 2005-04-29"
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "canda"
                     :looped-pali-word/translations [["en" "moon"]]}))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "suriya"
                     :looped-pali-word/translations [["en" "sun"]]}))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "kujagaha"
                     :looped-pali-word/translations [["en" "mars"]]}))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "medini"
                     :looped-pali-word/translations [["en" "earth"]]}))
    (time/freeze-clock! (time/parse "2005-04-30"))
    (sut/run-job! nil)
    (let [all (pali-db/list)]
      (is (= 1 (count all)))
      (is (= "suriya" (:pali-word/pali (first all)))))))
