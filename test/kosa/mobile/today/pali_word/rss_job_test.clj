(ns kosa.mobile.today.pali-word.rss-job-test
  (:require [kosa.mobile.today.pali-word.rss-job :as sut]
            [clojure.test :refer :all]
            [kuti.support.time :as time])
  (:import [java.net URI]))

(deftest parsing
  (testing "can shred pali/english, separated by em-dash"
    (is (= {:pali-word/pali "kuti"
            :pali-word/translations [["en" "hut"]]
            :pali-word/original-pali "kuti — hut"
            :pali-word/original-url (URI. "https://ignored")
            :pali-word/published-at (time/instant "2021-02-09T16:11:01.000Z")}
           (sut/parse* {:entries '({:description {:value "kuti — hut"}
                                    :uri "https://ignored"
                                    :published-date #inst "2021-02-09T16:11:01.000-00:00"})}))))

  (testing "tolerates RSS entries which are missing the em-dash"
    (is (= {:pali-word/pali "kuti = hut"
            :pali-word/translations [["en" ""]]
            :pali-word/original-pali "kuti = hut"
            :pali-word/original-url (URI. "https://ignored")
            :pali-word/published-at (time/instant "2021-02-09T16:11:01.000Z")}
           (sut/parse* {:entries '({:description {:value "kuti = hut"}
                                    :uri "https://ignored"
                                    :published-date #inst "2021-02-09T16:11:01.000-00:00"})}))))

  (testing "parses the published UTC date into a local date"
    (is (= {:pali-word/pali "kuti = hut"
            :pali-word/translations [["en" ""]]
            :pali-word/original-pali "kuti = hut"
            :pali-word/original-url (URI. "https://ignored")
            :pali-word/published-at (time/instant "2021-02-09T16:11:01.000Z")}
           (sut/parse* {:entries '({:description {:value "kuti = hut"}
                                    :uri "https://ignored"
                                    :published-date #inst "2021-02-09T16:11:01.000-00:00"})})))))
