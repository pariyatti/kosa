(ns ^:database kosa.mobile.today.looped-pali-word.publish-job-test
  (:require [clojure.test :refer :all]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kosa.mobile.today.looped-pali-word.publish-job :as sut]
            [kosa.mobile.today.looped-pali-word.db :as loop-db]
            [kosa.mobile.today.pali-word.db :as pali-db]
            [kuti.support.time :as time]))

(use-fixtures :once
  storage-fixtures/set-service-config)

(use-fixtures :each
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest publishing
  (testing "publishes a new pali word card from looped template"
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "tara"
                     :looped-pali-word/translations [["eng" "star"]]}))
    (sut/run-job! nil)
    (let [tara (pali-db/find-all :pali-word/pali "tara")]
      (is (= 1 (count tara))))))

(deftest ignores-an-empty-collection-of-looped-cards
  (testing "nothing happens (including no errors)"
    (is (nil? (sut/run-job! nil)))))

(deftest does-not-publish-more-than-once-per-day
  (testing "ignores a looped card it has already published"
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "abaddha"
                     :looped-pali-word/translations [["eng" "unfettered"]]}))
    (sut/run-job! nil)
    (sut/run-job! nil)
    (let [card (pali-db/find-all :pali-word/pali "abaddha")]
      (is (= 1 (count card))))))

(deftest looping
  (testing "does not ignore looped cards published on other days"
    (time/freeze-clock! (time/parse "2005-05-01"))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "abhaya"
                     :looped-pali-word/translations [["eng" "fearless"]]}))
    (sut/run-job! nil)
    (time/freeze-clock! (time/parse "2005-06-02"))
    (sut/run-job! nil)
    (let [cards (pali-db/find-all :pali-word/pali "abhaya")]
      (is (= 2 (count cards)))
      (is (= #{(time/parse "2005-05-01") (time/parse "2005-06-02")}
             (set (map :pali-word/published-at cards)))))))

(deftest scheduling-against-epoch
  (testing "publishes the Nth card from the 'perl epoch' on 2005-04-29"
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "canda"
                     :looped-pali-word/translations [["eng" "moon"]]}))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "suriya"
                     :looped-pali-word/translations [["eng" "sun"]]}))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "kujagaha"
                     :looped-pali-word/translations [["eng" "mars"]]}))
    (loop-db/save! (model/looped-pali-word
                    {:looped-pali-word/pali "medini"
                     :looped-pali-word/translations [["eng" "earth"]]}))
    (time/freeze-clock! (time/parse "2005-04-30"))
    (sut/run-job! nil)
    (let [all (pali-db/list)]
      (is (= 1 (count all)))
      (is (= "suriya" (:pali-word/pali (first all)))))))
