(ns kuti.record-test
  (:require [clojure.test :refer :all :exclude [time]]
            [xtdb.api :as xt]
            [kuti.support.debugging :refer :all]
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

(def record {:xt/id (->uuid "3291d680-0d70-4940-914d-35413e261115")
             :record     "vinyl"
             :artist     "The Who"})

(def record-without-id {:city "Igatpuri"
                        :state "Maharashtra"
                        :country "India"
                        :population 1234})

(def record-with-type1 {:kuti/type :mouse
                        :mouse/buttons 2
                        :mouse/brand "Logitech"})
(def record-with-type2 {:kuti/type :mouse
                        :mouse/buttons 3
                        :mouse/brand "Microsoft"})

(deftest db-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (core/put-async* record)]
      (xt/await-tx core/xtdb-node tx)
      (is (= record (sut/get "3291d680-0d70-4940-914d-35413e261115")))))

  (testing "put returns the record inserted"
    (let [inserted (sut/put record [:record :artist])]
      (is (= (:record record) (:record inserted)))
      (is (= (:artist record) (:artist inserted)))))

  (testing "put generates a new id"
    (let [inserted (sut/put record-without-id [:country :state :city :population])]
      (is (= record-without-id (dissoc inserted :xt/id)))
      (is (not (nil? (:xt/id inserted))))))

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
      (xt/await-tx core/xtdb-node inserted-record)
      (is (= new-record
             (sut/get "3291d680-0d70-4940-914d-35413e261115")))))

  (testing "put overwrites an existing record"
    (let [required-fields [:country :state :city :population]
          created (sut/put record-without-id required-fields)
          updated (sut/put (update created :population #(+ % 5555)) required-fields)]
      (is (= 1234 (:population created)))
      (is (= 6789 (:population updated)))
      (is (= (:xt/id created) (:xt/id updated))))))

(deftest db-list
  (testing "lists by :kuti/type and :[type]/updated-at"
    (time/unfreeze-clock!)
    (let [required-fields [:kuti/type :mouse/buttons :mouse/brand]
          m1 (sut/put (sut/timestamp record-with-type1) required-fields)
          _ (time/freeze-clock! (time/instant "1998-01-01T00:00:00.000Z"))
          m2 (sut/put (sut/timestamp record-with-type2) required-fields)]
      (is (= [m1 m2]
             (sut/list :mouse))))))

(deftest truncate
  (testing "destroys all of one type"
    (let [required-fields [:kuti/type :mouse/buttons :mouse/brand]
          m0 (sut/put (sut/timestamp {:kuti/type :not-deleted
                                      :not-deleted/name "hi2u"})
                      [:kuti/type :not-deleted/name])
          m1 (sut/put (sut/timestamp record-with-type1) required-fields)
          m2 (sut/put (sut/timestamp record-with-type2) required-fields)]
      (sut/truncate! :mouse)
      (is (= []
             (sut/list :mouse)))
      (is (= [m0]
             (sut/list :not-deleted))))))

(deftest publish-dates
  (testing "can query by :[type]/published-at"
    (time/unfreeze-clock!)
    (let [required-fields [:kuti/type :card/text]
          m1 (sut/put (sut/publish {:kuti/type :card
                                    :card/text "Settle your quarrels."})
                      required-fields)
          _ (time/freeze-clock! (time/instant "1998-01-01T00:00:00.000Z"))
          m2 (sut/put (sut/publish {:kuti/type :card
                                    :card/text "Settle your quarrels."})
                      required-fields)
          list-query '{:find     [e published-at]
                       :where    [[e :card/published-at published-at]]
                       :order-by [[published-at :asc]]}]
      (is (= [m2 m1]
             (sut/query list-query)))))

  (testing "can publish in the past/future"
    (let [required-fields [:kuti/type :essay/title :essay/author]
          m1 (sut/put (sut/publish-at {:kuti/type :essay
                                       :essay/title "Manual of Perfections"
                                       :essay/author "Ledi Sayadaw"}
                                      (time/instant "2100-01-01T00:00:00.000Z"))
                      required-fields)
          m2 (sut/put (sut/publish-at {:kuti/type :essay
                                       :essay/title "Pali Canon"
                                       :essay/author "Ananda"}
                                      (time/date-time (time/date time/BCE 250)))
                      required-fields)
          list-query '{:find     [e published-at]
                       :where    [[e :essay/published-at published-at]]
                       :order-by [[published-at :asc]]}]
      (is (= [m2 m1]
             (sut/query list-query)))))

  (testing "can draft (unpublish)"
    (let [required-fields [:kuti/type :book/title :book/author]
          m1 (sut/put (sut/publish-at {:kuti/type :book
                                       :book/title "Parami Dipani"
                                       :book/author "Ledi Sayadaw"}
                                      (time/instant "1868-01-01T00:00:00.000Z"))
                      required-fields)
          _ (sut/put (sut/draft m1) required-fields)
          list-query '{:find     [e published-at]
                       :where    [[e :book/published-at published-at]]
                       :order-by [[published-at :asc]]}]
      (is (= 1 (count (sut/query list-query))))
      (is (= time/DRAFT-DATE
             (-> (sut/query list-query) first :book/published-at))))))
