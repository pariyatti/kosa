(ns kosa.mobile.today.looped.publish-job-test
  (:require [clojure.test :refer :all]
            [kosa.mobile.today.looped.publish-job :as sut]
            [kuti.support.time :as time]
            [tick.alpha.api :as t]))

(deftest publish-time-is-always-7am-pst-same-day
  ;; 15:11 UTC is 07:11 PST
  (testing "at the start of the day UTC, pretend to publish at 7am PST"
    (is (= (t/instant "2012-06-15T15:11:02Z")
           (sut/publish-time (t/instant "2012-06-15T00:00:01Z")))))

  (testing "at the end of the day UTC, pretend to publish at 7am PST"
    (is (= (t/instant "2012-06-15T15:11:02Z")
           (sut/publish-time (t/instant "2012-06-15T23:59:59Z"))))))
