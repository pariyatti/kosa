(ns kuti.support.types-test
  (:require [clojure.test :refer :all]
            [kuti.support.types :as sut]))

(deftest typify
  (testing "adds ns type to keyword"
    (is (= :some-ns/some-kw
           (sut/typify {:kuti/type :some-ns} :some-kw))))

  (testing "adds long ns type to keyword"
    (is (= :some.long-ns/some-kw
           (sut/typify {:kuti/type :some.long-ns} :some-kw))))

  (testing "ignores entities without :kuti/type"
    (is (= :some-kw
           (sut/typify {:name "Ledi Sayadaw"} :some-kw)))))

(deftest dup
  (testing "includes new :kuti/type"
    (is (= {:kuti/type :zag
            :zag/age 123
            :zag/name "Smith"}
           (sut/dup {:kuti/type :zig
                     :zig/age 123
                     :zig/name "Smith"}
                    :zag))))

  (testing "does not overwrite nested types"
    (is (= {:kuti/type :zag
            :zag/age 123
            :zag/name "Smith"
            :zag/attachment {:kuti/type :attm
                             :attachment/id 789
                             :attachment/url "https://pariyatti.org/some.mp3"}}
           (sut/dup {:kuti/type :zig
                     :zig/age 123
                     :zig/name "Smith"
                     :zig/attachment {:kuti/type :attm
                                      :attachment/id 789
                                      :attachment/url "https://pariyatti.org/some.mp3"}}
                    :zag)))))
