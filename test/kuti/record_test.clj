(ns kuti.record-test
  (:require [clojure.test :refer :all :exclude [time]]
            [crux.api]
            [kuti.support.time :as time]
            [kuti.support.digest :refer [->uuid]]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.record.core :as core]
            [kuti.record :as sut])
  (:import [java.lang IllegalArgumentException AssertionError]))

(use-fixtures :once
  record-fixtures/load-states
  time-fixtures/freeze-clock)

(def record {:crux.db/id (->uuid "3291d680-0d70-4940-914d-35413e261115")
             :updated-at @time/clock
             :record     "vinyl"
             :artist     "The Who"})

(def record-without-id {:city "Igatpuri"
                        :state "Maharashtra"
                        :country "India"
                        :population 1234})

(def record-with-type1 {:type :mouse
                        :mouse/buttons 2
                        :mouse/brand "Logitech"})
(def record-with-type2 {:type :mouse
                        :mouse/buttons 3
                        :mouse/brand "Microsoft"})

(deftest db-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (core/put-async* record)]
      (crux.api/await-tx core/crux-node tx)
      (is (= record (sut/get "3291d680-0d70-4940-914d-35413e261115")))))

  (testing "put returns the record inserted"
    (let [inserted (sut/put record [:record :artist])]
      (is (= (:record record) (:record inserted)))
      (is (= (:artist record) (:artist inserted)))))

  (testing "put generates a new id"
    (let [inserted (sut/put record-without-id [:country :state :city :population])]
      (is (= record-without-id (dissoc inserted :crux.db/id :updated-at)))
      (is (not (nil? (:crux.db/id inserted))))))

  (testing "put barfs on badly-formed documents"
    (is (thrown-with-msg? java.lang.Exception #"Extra fields ':superfluous-field' found during put."
                          (sut/put {:city "Igatpuri"
                                    :superfluous-field "I should cause an error."}
                                   [:city])))))

(deftest db-update-operations
  (testing "Can update raw datums"
    (let [_               (core/put-async* record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (core/put-async* new-record)]
      (crux.api/await-tx core/crux-node inserted-record)
      (is (= new-record
             (sut/get "3291d680-0d70-4940-914d-35413e261115")))))

  (testing "put overwrites an existing record"
    (let [required-fields [:country :state :city :population]
          created (sut/put record-without-id required-fields)
          updated (sut/put (update created :population #(+ % 5555)) required-fields)]
      (is (= 1234 (:population created)))
      (is (= 6789 (:population updated)))
      (is (= (:crux.db/id created) (:crux.db/id updated))))))

(deftest db-list
  (testing "lists by :type and :[type]/updated-at"
    (let [required-fields [:type :mouse/buttons :mouse/brand]
          m1 (sut/put (sut/timestamp record-with-type1) required-fields)
          _ (time/freeze-clock! (time/instant "1998-01-01T00:00:00.000Z"))
          m2 (sut/put (sut/timestamp record-with-type2) required-fields)]
      (is (= [m1 m2]
             (sut/list :mouse))))))
