(ns kutis.support.time-test
  (:require [clojure.test :refer [deftest testing is]]
            [kutis.support.time :as time]))

(deftest roundtrip
  (testing "can roundtrip from a UTC time-zoned offset-date-time"
    (let [zoned (time/parse "2021-02-11T22:26:06.808-00:00")
          s (time/string zoned)
          i (time/instant s)
          s2 (time/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2))))

  (testing "can roundtrip from time-zoned time offset-date-time"
    (let [zoned (time/parse "2021-02-11T22:26:06.808-06:00")
          s (time/string zoned)
          i (time/instant s)
          s2 (time/string i)]
      (is (= "2021-02-12T04:26:06.808Z" s2))))

  (testing "can roundtrip from a UTC time-zoned clojure reader inst"
    (let [inst #inst "2021-02-11T22:26:06.808-00:00"
          s (time/string inst)
          i (time/instant s)
          s2 (time/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2))))

  (testing "can roundtrip from a UTC time-zoned clojure reader inst with no millis"
    (let [inst #inst "2021-02-11T00:00:00.000-00:00"
          s (time/string inst)
          i (time/instant s)
          s2 (time/string i)]
      (is (= "2021-02-11T00:00:00.000Z" s2))))

  (testing "can roundtrip from a time-zoned clojure reader inst"
    (let [inst #inst "2021-02-11T16:26:06.808-06:00"
          s (time/string inst)
          i (time/instant s)
          s2 (time/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2))))

  (testing "can roundtrip an instant with no time-zone"
    (let [inst #inst "2021-02-11T22:26:06.808Z"
          s (time/string inst)
          i (time/instant s)
          s2 (time/string i)]
      (is (= "2021-02-11T22:26:06.808Z" s2)))))
