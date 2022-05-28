(ns kuti.storage.open-uri-test
  (:require [clojure.test :refer :all]
            [kuti.storage.open-uri :as sut]))

(deftest failed-download-throws-exception
  (testing "catching the exception early makes txt error handling complicated; just bomb"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Body was repeatedly empty."
                          (sut/download-uri! "http://does-not-exist.pariyatti.org")))))
