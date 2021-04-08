(ns kuti.support.types-test
  (:require [clojure.test :refer :all]
            [kuti.support.types :as sut]))

(deftest typify
  (testing "adds ns type to keyword"
    (is (= :some-ns/some-kw
           (sut/typify {:type :some-ns} :some-kw))))

  (testing "adds long ns type to keyword"
    (is (= :some.long-ns/some-kw
           (sut/typify {:type :some.long-ns} :some-kw))))

  (testing "ignores entities without :type"
    (is (= :some-kw
           (sut/typify {:name "Ledi Sayadaw"} :some-kw)))))
