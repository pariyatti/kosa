(ns kutis.controller-test
  (:require [clojure.test :refer :all]
            [kutis.controller :as sut]
            [clojure.java.io :as io]))

(deftest document-timestamps
  (testing "timestamp with published-at when it isn't set"
    (let [doc (sut/params->doc {} {})]
      (is (not (nil? (:published-at doc))))))

  ;; TODO: marker value for draft? :published-at = "9999-01-01" or something?

  (testing "do not timestamp with published-at if it is already set"
    (let [doc (sut/params->doc {:published-at #inst "1981-07-30"} [])]
      (is (not (nil? (:published-at doc))))
      (is (= #inst "1981-07-30" (:published-at doc))))))

(deftest fields-mapped-by-name
  (testing "fields listed as keywords pass directly from params into the doc"
    (let [params {:card-type "pali_word"
                  :pali "kuti"}
          doc (sut/params->doc params [:card-type :pali])]
      (is (= "pali_word" (:card-type doc)))
      (is (= "kuti" (:pali doc)))))

  (testing "barf on fields listed with non-keyword, non-lambda types"
    (let [params {:corrupt "this will fail"}]
      (is (thrown-with-msg? java.lang.Exception #"Parameter mapping 'corrupt' is not a keyword or fn."
                            (sut/params->doc params ["corrupt"])))))

  (testing "accepts an empty mapping list"
    (let [doc (sut/params->doc {:some-file (io/file "zig")} [])]
      (is (get doc :published-at)))))

(deftest fields-mapped-from-lambda
  (testing "executes a lambda from mapping list"
    (let [params {:language ["en" "fr" "hi"]
                  :translation ["cat" "chat" "बिल्ली"]}
          doc (sut/params->doc params
                               [[:translations #(map vector (:language %) (:translation %))]])]
      (is (= [["en" "cat"]
              ["fr" "chat"]
              ["hi" "बिल्ली"]]
             (:translations doc))))))
