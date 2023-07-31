(ns kuti.storage.open-uri-test
  (:require [clojure.test :refer :all]
            [kuti.storage.open-uri :as sut]))

(deftest failed-download-throws-exception
  (testing "catching the exception early makes txt error handling complicated; just bomb"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Body was repeatedly empty."
                          (sut/download-uri! "http://does-not-exist.pariyatti.org")))))

;; use to examine weird SSL Handshake failures coming from download.pariyatti.org
#_(deftest inspect-broken-url
  (testing "let us see"
    (is (= "hi2u"
           (sut/download-uri! "https://download.pariyatti.org/dwob/itivuttaka_1_17.mp3")))))
