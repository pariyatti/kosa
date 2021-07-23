(ns ^:database kosa.mobile.today.looped-words-of-buddha.publish-job-test
  (:require [clojure.test :refer :all]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kosa.mobile.today.looped-words-of-buddha.publish-job :as sut]
            [kosa.mobile.today.looped-words-of-buddha.db :as loop-db]
            [kosa.mobile.today.words-of-buddha.db :as buddha-db]
            [kuti.support.time :as time]))

(use-fixtures :each
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest publishing
  (testing "publishes a new words-of-buddha card from looped template"
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "tara"
                     :looped-words-of-buddha/translations [["eng" "star"]]}))
    (sut/run-job! nil)
    (let [tara (buddha-db/find-all :words-of-buddha/words "tara")]
      (is (= 1 (count tara))))))

(deftest ignores-an-empty-collection-of-looped-cards
  (testing "nothing happens (including no errors)"
    (is (nil? (sut/run-job! nil)))))

(deftest does-not-publish-more-than-once-per-day
  (testing "ignores a looped card it has already published"
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "abaddha"
                     :looped-words-of-buddha/translations [["eng" "unfettered"]]}))
    (sut/run-job! nil)
    (sut/run-job! nil)
    (let [card (buddha-db/find-all :words-of-buddha/words "abaddha")]
      (is (= 1 (count card))))))

(deftest looping
  (testing "does not ignore looped cards published on other days"
    (time/freeze-clock! (time/parse "2005-05-01"))
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "abhaya"
                     :looped-words-of-buddha/translations [["eng" "fearless"]]}))
    (sut/run-job! nil)
    (time/freeze-clock! (time/parse "2005-06-02"))
    (sut/run-job! nil)
    (let [cards (buddha-db/find-all :words-of-buddha/words "abhaya")]
      (is (= 2 (count cards)))
      (is (= #{(time/parse "2005-05-01") (time/parse "2005-06-02")}
             (set (map :words-of-buddha/published-at cards)))))))

(deftest scheduling-against-epoch
  (testing "publishes the Nth card from the 'perl epoch' on 2005-04-29"
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "canda"
                     :looped-words-of-buddha/translations [["eng" "moon"]]}))
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "suriya"
                     :looped-words-of-buddha/translations [["eng" "sun"]]}))
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "kujagaha"
                     :looped-words-of-buddha/translations [["eng" "mars"]]}))
    (loop-db/save! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "medini"
                     :looped-words-of-buddha/translations [["eng" "earth"]]}))
    (time/freeze-clock! (time/parse "2005-04-30"))
    (sut/run-job! nil)
    (let [all (buddha-db/list)]
      (is (= 1 (count all)))
      (is (= "suriya" (:words-of-buddha/words (first all)))))))
