(ns kuti.support.time-test
  (:require [clojure.test :refer [deftest testing is]]
            [tick.alpha.api :as t]
            [kuti.support.time :as sut]
            [kuti.fixtures.time-fixtures :as time-fixtures]))

(deftest roundtrip
  (testing "can roundtrip from a UTC time-zoned offset-date-time"
    (let [zoned (sut/parse-tz "2021-02-11T22:26:06.808-00:00")
          s (sut/string zoned)
          i (sut/instant s)
          s2 (sut/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2))))

  (testing "can roundtrip from time-zoned time offset-date-time"
    (let [zoned (sut/parse-tz "2021-02-11T22:26:06.808-06:00")
          s (sut/string zoned)
          i (sut/instant s)
          s2 (sut/string i)]
      (is (= "2021-02-12T04:26:06.808Z" s2))))

  (testing "can roundtrip from a UTC time-zoned clojure reader inst"
    (let [inst #inst "2021-02-11T22:26:06.808-00:00"
          s (sut/string inst)
          i (sut/instant s)
          s2 (sut/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2))))

  (testing "can roundtrip from a UTC time-zoned clojure reader inst with no millis"
    (let [inst #inst "2021-02-11T00:00:00.000-00:00"
          s (sut/string inst)
          i (sut/instant s)
          s2 (sut/string i)]
      (is (= "2021-02-11T00:00:00.000Z" s2))))

  (testing "can roundtrip from a time-zoned clojure reader inst"
    (let [inst #inst "2021-02-11T16:26:06.808-06:00"
          s (sut/string inst)
          i (sut/instant s)
          s2 (sut/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2))))

  (testing "can roundtrip an instant with no time-zone"
    (let [inst #inst "2021-02-11T22:26:06.808Z"
          s (sut/string inst)
          i (sut/instant s)
          s2 (sut/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2)))))

(deftest parse
  (testing "returns a UTC instant for dates"
    (is (= (sut/instant "2007-02-02T00:00:00.000Z")
           (sut/parse   "2007-02-02"))))

  (testing "returns a UTC instant for date-times"
    (is (= (sut/instant "2007-02-02T17:17:00.000Z")
           (sut/parse   "2007-02-02T17:17"))))

  (testing "strip zulu marker before parsing to deal with all non-milli positions"
    (is (= (sut/instant "1991-01-01T00:00:00.000Z")
           (sut/parse   "1991-01-01T00:00:00Z"))))

  (testing "ignores sub-millisecond precision"
    (is (= (sut/instant "1991-01-01T00:00:00.000Z")
           (sut/parse   "1991-01-01T00:00:00.000000Z"))))

  (testing "rejects localized date-times with tz"
    (is (thrown? IllegalArgumentException
                 (sut/parse "2000-01-01T00:00:00Z[Europe/Paris]"))))

  (testing "rejects localized date-times with offset"
    (is (thrown? IllegalArgumentException
                 (sut/parse "2021-02-11T16:26:06.808-06:00")))
    (is (thrown? IllegalArgumentException
                 (sut/parse "2021-02-11T16:26:06.808+08:00")))))

(deftest publishing-dates
  (testing "equates 300 BCE to the year -299"
    (let [by-era (sut/date sut/BCE 300 1 1)
          no-era (sut/date -299 1 1)]
      (is (= #time/date "-0299-01-01"
             by-era
             no-era))))

  (testing "refuses negative years for BCE"
    (is (thrown-with-msg? java.lang.IllegalArgumentException
                          #"Negative year '-350' supplied for BCE date."
                          (sut/date sut/BCE -350 1 1))))

  (testing "creates simple ancient dates with only the year"
    (is (= (sut/date sut/BCE 400 1 1)
           (sut/date sut/BCE 400)))))

(deftest publishing-date-times
  (testing "creates an instant in the BCE era from date + time"
    (is (= #time/instant "-0349-01-01T13:35:22Z"
           (sut/date-time (sut/date sut/BCE 350)
                          (sut/time 13 35 22)))))

  (testing "creates an instant with empty time if only date is provided"
    (is (= #time/instant "-0349-01-01T00:00:00Z"
           (sut/date-time (sut/date sut/BCE 350)))))

  (testing "creates an instant from an instant (tolerant)"
    (is (= #time/instant "-0349-01-01T13:35:22Z"
           (sut/date-time #time/instant "-0349-01-01T13:35:22Z")))))

(deftest days-between
  (testing "can diff days between a string date and a 'now' instant"
    (sut/freeze-clock! time-fixtures/win95)
    (is (= 5
           (sut/days-between "1995-08-19"
                             (sut/now))))))

(deftest conform-8601
  (testing "ensures period char for 0-digit format returned from xtdb"
    (is (= "2021-03-21T00:00:00.000Z"
           (sut/to-8601-string #inst "2021-03-21T00:00:00Z"))))

  (testing "ensures 3-digit ms for 6-digit format returned from xtdb"
    (is (=  "2021-03-22T01:23:45.123Z"
           (sut/to-8601-string #inst "2021-03-22T01:23:45.123456Z"))))

  (testing "ensures period char for 9-digit format returned from xtdb"
    (is (=  "2021-03-22T01:23:45.123Z"
           (sut/to-8601-string #inst "2021-03-22T01:23:45.123456789Z")))))
