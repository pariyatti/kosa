(ns kuti.support-test
  (:require [kuti.support :as sut]
            [clojure.test :refer :all]))

(deftest assoc-unless
  (testing "associates when key doesn't exist"
    (is (= "value"
           (-> (sut/assoc-unless {} :new-key "value")
               :new-key))))

  (testing "ignores when key already exists"
    (is (= "old value"
           (-> {:old-key "old value"}
               (sut/assoc-unless :old-key "new value")
               :old-key)))))
