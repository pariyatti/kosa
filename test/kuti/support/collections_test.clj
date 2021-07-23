(ns kuti.support.collections-test
  (:require [kuti.support.collections :as sut]
            [clojure.test :refer :all]))

(deftest merging-key-values
  (testing "merges kvs by key, not value"
    (is (= [["eng" "moon"]
            ["fra" "lune"]
            ["spa" "luna"]]
           (sut/merge-kvs [["eng" "mon"]
                           ["fra" "lune"]]
                          [["eng" "moon"]
                           ["spa" "luna"]]))))

  (testing "detects subsets by key and value"
    (is (sut/subset-kvs? [["eng" "moon"]
                          ["spa" "luna"]]
                         [["eng" "moon"]
                          ["fra" "lune"]
                          ["spa" "luna"]]))

    (is (not (sut/subset-kvs? [["eng" "moon"]
                               ["spa" "luna"]]
                              [["eng" "mon"]
                               ["fra" "lune"]
                               ["spa" "luna"]]))))

  (testing "detects identical subsets"
    (is (sut/subset-kvs? [["eng" "moon"]]
                         [["eng" "moon"]]))))
