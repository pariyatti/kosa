(ns kosa.mobile.today.looped-pali-word.txt-test
  (:require [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.fixtures.model-fixtures :as model]
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

(deftest parsing-txt-file
  (testing "parses out text without whitespace or separators"
    (let [f (file-fixtures/file "pali_word_raw.txt")
          txt (slurp f)]
      (is (= [{:looped-pali-word/pali "vimutti"
               :looped-pali-word/translations [["en" "freedom, release, deliverance, emancipation, liberation"]]}
              {:looped-pali-word/pali "kataññū"
               :looped-pali-word/translations [["en" "kata + ññū = what is done + knowing, acknowledging what has been done (to, for one), grateful"]]}
              {:looped-pali-word/pali "tarati"
               :looped-pali-word/translations [["en" "to cross [a river], to surmount, overcome [the great flood of life, desire, ignorance], to get to the other side, to cross over, as in crossing the ocean of suffering"]]}]
             (looped/parse i txt "en"))))))

(deftest ingesting-txt-file
  (testing "inserts entries into db"
    (let [f (file-fixtures/file "pali_word_raw.txt")]
      (sut/ingest f "en")
      (is (= 3 (count (db/list)))))))

(deftest merging-new-entities
  (testing "ignore identical entities"
    (db/save! (model/looped-pali-word
               {:looped-pali-word/pali "suriya"
                :looped-pali-word/translations [["en" "sun"]]
                :looped-pali-word/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "suriya"
                     :looped-pali-word/translations [["en" "sun"]]
                     :looped-pali-word/published-at (time/parse "2012-01-01")}))
    (let [suriya (db/q :looped-pali-word/pali "suriya")]
      (is (= 1 (count  suriya)))
      (is (= (time/parse "2008-01-01")
             (-> suriya first :looped-pali-word/published-at)))))

  (testing "merge additional languages if merged is not identical"
    (db/save! (model/looped-pali-word
               {:looped-pali-word/pali "canda"
                :looped-pali-word/translations [["en" "moon"]
                                                ["hi" "चंद"]]
                :looped-pali-word/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "canda"
                     :looped-pali-word/translations [["fr" "lune"]
                                                     ["es" "luna"]]
                     :looped-pali-word/published-at (time/parse "2012-01-01")}))
    (let [canda (db/q :looped-pali-word/pali "canda")]
      (is (= 1 (count  canda)))
      (is (= [["en" "moon"]
              ["hi" "चंद"]
              ["fr" "lune"]
              ["es" "luna"]]
             (-> canda first :looped-pali-word/translations))))))

(deftest indexing
  (testing "index auto-increments"
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "tara"
                     :looped-pali-word/translations [["en" "star"]]}))
    (looped/db-insert! i (model/looped-pali-word
                    {:looped-pali-word/pali "kujagaha"
                     :looped-pali-word/translations [["en" "mars"]]}))
    (let [tara (db/q :looped-pali-word/pali "tara")
          kujagaha (db/q :looped-pali-word/pali "kujagaha")]
      (is (= 1 (- (-> kujagaha first :looped-pali-word/index)
                  (-> tara first :looped-pali-word/index)))))))
