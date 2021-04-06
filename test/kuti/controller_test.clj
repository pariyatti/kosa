(ns kuti.controller-test
  (:require [clojure.test :refer :all]
            [kuti.controller :as sut]
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

(deftest fields-mapped-by-name-directly
  (testing "fields listed as keywords pass directly from params into the doc"
    (let [params {:type "pali_word"
                  :pali "kuti"}
          doc (sut/params->doc params [:type :pali])]
      (is (= "pali_word" (:type doc)))
      (is (= "kuti" (:pali doc)))))

  (testing "barf on fields listed with non-keyword, non-lambda types"
    (let [params {:corrupt "this will fail"}]
      (is (thrown-with-msg? java.lang.Exception #"Parameter mapping 'corrupt' is not a keyword or fn."
                            (sut/params->doc params ["corrupt"])))))

  (testing "accepts an empty mapping list"
    (let [doc (sut/params->doc {:some-file (io/file "zig")} [])]
      (is (get doc :published-at)))))

(deftest fields-mapped-by-alias
  (testing "aliased fields get the alias as their name"
    (let [params {:type :pali-word
                  :pali "kuti"}
          doc (sut/params->doc params [:type
                                       [:pali :pali-word/pali]])]
      (is (= :pali-word (:type doc)))
      (is (= "kuti" (:pali-word/pali doc)))
      (is (nil? (:pali doc))))))

(deftest fields-mapped-from-lambda
  (testing "executes a lambda from mapping list"
    (let [params {:language ["en" "fr" "hi"]
                  :translation ["cat" "chat" "बिल्ली"]}
          doc (sut/params->doc params
                               [[:translations
                                 #(map vector (:language %) (:translation %))]])]
      (is (= [["en" "cat"]
              ["fr" "chat"]
              ["hi" "बिल्ली"]]
             (:translations doc))))))

(deftest namespaced-fields
  (testing "adds a namespace to keyword params"
    (let [params {:type :pali-word-card
                  :pali "kuti"}
          doc (sut/namespaced :pali-word params)]
      (is (= :pali-word-card (:pali-word/type doc)))
      (is (nil? (:type doc)))
      (is (= "kuti" (:pali-word/pali doc)))
      (is (nil? (:pali doc)))))

  (testing "namespaced rejects non-keyword ns"
    (let [params {:pali "kuti"}]
      (is (thrown? java.lang.AssertionError
                   (sut/namespaced "pali-word" params)))))

  (testing "namespaced rejects non-keyword keys"
    (let [params {"type" :pali-word-card}]
      (is (thrown? java.lang.AssertionError
                   (sut/namespaced :pali-word params))))))
