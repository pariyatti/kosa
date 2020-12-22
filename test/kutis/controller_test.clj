(ns kutis.controller-test
  (:require [clojure.test :refer :all]
            [kutis.controller :as sut]))

(deftest document-timestamps
  (testing "timestamp with published-at when it isn't set"
    (let [doc (sut/params->doc {} {})]
      (is (not (nil? (:published-at doc))))))

  (testing "do not timestamp with published-at if it is already set"
    (let [doc (sut/params->doc {:published-at #inst "1981-07-30"} [])]
      (is (not (nil? (:published-at doc))))
      (is (= #inst "1981-07-30" (:published-at doc))))))

(deftest field-from-lambda
  (testing "executes a lambda from mapping list"
    (let [params {:language ["en" "fr" "hi"]
                  :translation ["cat" "chat" "बिल्ली"]}
          doc (sut/params->doc params
                               [[:translations #(map vector (:language %) (:translation %))]])]
      (is (= [["en" "cat"]
              ["fr" "chat"]
              ["hi" "बिल्ली"]]
             (:translations doc))))))
