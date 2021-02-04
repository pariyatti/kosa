(ns kosa.mobile.today.pali-word.rss-job-test
  (:require [kosa.mobile.today.pali-word.rss-job :as sut]
            [kosa.mobile.today.pali-word.db :as db]
            [clojure.test :refer :all]
            [kutis.fixtures.record-fixtures :as fixtures]))

(use-fixtures :each fixtures/load-states)

(deftest ^:integration polling
  (testing "only gets feed updates once"
    (let [first-feed (sut/poll)
          second-feed (sut/poll)]
      (is (not (nil? first-feed)))
      (is (nil? second-feed)))))

(deftest parsing
  (testing "barfs when feed nodes can't be parsed"
    (is (thrown? java.lang.ClassCastException
                 (sut/parse {:entries '({:description {:value {:a-string "is expected here"}}
                                         :uri "https://ignored"})}))))

  (testing "barfs when feed nodes are mis-ordered"
    (is (thrown? clojure.lang.ExceptionInfo
                 (sut/parse {:entries '({:description {:value-not-found "<html>wrong node</html>"}
                                         :uri "https://ignored"})})))
    (is (thrown? clojure.lang.ExceptionInfo
                 (sut/parse {:entries '({:description {:value "some <br /> html"}
                                         :uri-not-found "https://ignored"})}))))

  (testing "can shred pali/english, separated by hyphen"
    (is (= {:pali "kuti"
            :translations [["en" "hut"]]
            :original-pali "kuti — hut"
            :original-url "https://ignored"}
           (sut/parse* {:entries '({:description {:value "kuti — hut"}
                                    :uri "https://ignored"})}))))

  (testing "tolerates RSS entries which are missing the hyphen"
    (is (= {:pali "kuti = hut"
            :translations [["en" ""]]
            :original-pali "kuti = hut"
            :original-url "https://ignored"}
           (sut/parse* {:entries '({:description {:value "kuti = hut"}
                                    :uri "https://ignored"})})))))

(deftest database
  (testing "does not insert the same entity twice"
    (let [feed {:entries '({:description {:value "anta — end, goal, limit"}
                            :uri "https://ignored"})}]
      (sut/parse feed)
      (sut/parse feed)
      (is (= 1 (count (db/list)))))))