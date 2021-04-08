(ns kuti.controller-test
  (:require [clojure.test :refer :all]
            [kuti.controller :as sut]
            [clojure.java.io :as io])
  (:import [java.lang IllegalArgumentException]))

(deftest fields-mapped-by-name-directly
  (testing "fields listed as keywords pass directly from params into the doc"
    (let [params {:type :pali-word-of-the-day
                  :pali "kuti"}
          doc (sut/params->doc params [:type :pali])]
      (is (= :pali-word-of-the-day (:kuti/type doc)))
      (is (= "kuti" (:pali doc)))))

  (testing "barf on fields listed with non-keyword, non-lambda types"
    (let [params {:corrupt "this will fail"}]
      (is (thrown-with-msg? java.lang.Exception #"Parameter mapping 'corrupt' is not a keyword or fn."
                            (sut/params->doc params ["corrupt"])))))

  (testing "does not accept an empty mapping list"
    (is (thrown-with-msg? java.lang.IllegalArgumentException
                          #"Controller cannot map params if no fields are specified."
                          (sut/params->doc {:some-file (io/file "zig")} [])))))

(deftest fields-mapped-by-alias
  (testing "aliased fields get the alias as their name"
    (let [params {:type :pali-word
                  :pali "kuti"}
          doc (sut/params->doc params [:type
                                       [:pali :pali-word/pali]])]
      (is (= :pali-word (:kuti/type doc)))
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

(deftest docs-with-type
  (testing "rejects non-keyword types"
    (is (thrown-with-msg? IllegalArgumentException
                          #":kuti/type key must be a keyword."
                          (sut/params->doc {:type "not_a_keyword_param"
                                            :name "Steven"}
                                           [:type :name]))))

  (testing "maps :type to :kuti/type"
    (is (= :user
           (:kuti/type (sut/params->doc {:type :user
                                         :name "Steven"}
                                        [:type :name])))))

  (testing "permits params without :type"
    (is (= "Steven"
           (:name (sut/params->doc {:name "Steven"}
                                        [:name]))))))

(deftest namespaced-fields
  (testing "adds a namespace to keyword params"
    (let [params {:type :pali-word-card
                  :pali "kuti"}
          doc (sut/namespaced :pali-word params)]
      (is (= :pali-word-card (:pali-word/type doc)))
      (is (nil? (:kuti/type doc)))
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
