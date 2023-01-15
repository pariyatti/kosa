(ns kosa.mobile.today.looped.publish-job-test
  (:require [clojure.test :refer :all]
            [kosa.mobile.today.looped.publish-job :as sut]
            [kuti.support.time :as time]
            [tick.alpha.api :as t]))

(deftype StubPublisher []
  sut/Publisher
  (type [_] :stub)
  (offset [_] "00:11:02")
  (main-key [_] :stub/nothing)
  (published-at-key [_] :stub/published-at)
  (looped-list [_] [])
  (looped-find [_ idx] [])
  (entity-find [_ card] [])
  (save! [_ card] nil))

(deftest publish-time-is-always-7am-pst-same-day
  ;; PST is UTC+08:00 for our purposes
  ;; TODO: rather than publishing in PST, as the email and RSS scripts do,
  ;;       should we just publish in UTC directly? it seems less confusing.
  (testing "at the start of the day UTC, pretend to publish at `offset` in PST"
    (is (= (t/instant "2012-06-15T08:11:02Z")
           (sut/publish-time (StubPublisher.) (t/instant "2012-06-15T00:00:01Z")))))

  (testing "at the end of the day UTC, pretend to publish at `offset` in PST"
    (is (= (t/instant "2012-06-15T08:11:02Z")
           (sut/publish-time (StubPublisher.) (t/instant "2012-06-15T23:59:59Z"))))))
