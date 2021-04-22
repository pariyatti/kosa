(ns kuti.support.collections-test
  (:require [kuti.support.collections :as sut]
            [clojure.test :refer :all]))

(deftest merging-key-values
  (testing "merges kvs by key, not value"
    (is (= [["en" "moon"]
            ["fr" "lune"]
            ["es" "luna"]]
           (sut/merge-kvs [["en" "mon"]
                           ["fr" "lune"]]
                          [["en" "moon"]
                           ["es" "luna"]]))))

  (testing "detects subsets by key and value"
    (is (sut/subset-kvs? [["en" "moon"]
                           ["es" "luna"]]
                         [["en" "moon"]
                          ["fr" "lune"]
                          ["es" "luna"]]))

    (is (not (sut/subset-kvs? [["en" "moon"]
                               ["es" "luna"]]
                              [["en" "mon"]
                               ["fr" "lune"]
                               ["es" "luna"]]))))

  (testing "detects identical subsets"
    (is (sut/subset-kvs? [["en" "moon"]]
                         [["en" "moon"]]))))
