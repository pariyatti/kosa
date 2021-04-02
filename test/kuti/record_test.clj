(ns kuti.record-test
  (:require [clojure.test :refer :all :exclude [time]]
            [crux.api]
            [kuti.support.time :as time]
            [kuti.support.digest :refer [->uuid]]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
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

(deftest db-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (sut/put-async* record)]
      (crux.api/await-tx sut/crux-node tx)
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
    (let [_               (sut/put-async* record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (sut/put-async* new-record)]
      (crux.api/await-tx sut/crux-node inserted-record)
      (is (= new-record
             (sut/get "3291d680-0d70-4940-914d-35413e261115")))))

  (testing "put overwrites an existing record"
    (let [required-fields [:country :state :city :population]
          created (sut/put record-without-id required-fields)
          updated (sut/put (update created :population #(+ % 5555)) required-fields)]
      (is (= 1234 (:population created)))
      (is (= 6789 (:population updated)))
      (is (= (:crux.db/id created) (:crux.db/id updated))))))

(deftest save!
  (testing "requires a :type"
    (is (thrown-with-msg? java.lang.AssertionError
                          #":type key expected"
                          (sut/save! {:user/name "Vikram"}))))

  (testing "implied type must match :type"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Some keys did not match specified :type. :user/name, :address/street"
                          (sut/save! {:type :essay
                                      :user/name "Vikram"
                                      :address/street "Main St."}))))

  (testing "saves doc with correctly-typed keys"
    (let [_ (sut/put {:db.entity/type :essay
                      :db.entity/attrs [:essay/title :essay/bookmarked]}
                     [:db.entity/type :db.entity/attrs])
          _ (sut/put {:db/ident     :essay/title
                      :db/valueType :db.type/string}
                     [:db/ident :db/valueType])
          _ (sut/put {:db/ident     :essay/bookmarked
                      :db/valueType :db.type/boolean}
                     [:db/ident :db/valueType])
          saved (sut/save! {:type             :essay
                            :essay/title      "Strength of the Record"
                            :essay/bookmarked true})
          found (sut/get (:crux.db/id saved))]
      (is (= {:updated-at       @time/clock
              :type             :essay
              :essay/title      "Strength of the Record"
              :essay/bookmarked true}
             (dissoc found :crux.db/id)))))

  (testing "rejects doc with missing keys"
    (let [_ (sut/put {:db.entity/type  :test
                      :db.entity/attrs [:test/bp :test/hr :test/record-date]}
                     [:db.entity/type :db.entity/attrs])
          _ (sut/put {:db/ident     :test/bp
                      :db/valueType :db.type/bigint}
                     [:db/ident :db/valueType])
          _ (sut/put {:db/ident     :test/hr
                      :db/valueType :db.type/bigdec}
                     [:db/ident :db/valueType])
          _ (sut/put {:db/ident     :test/record-date
                      :db/valueType :db.type/instant}
                     [:db/ident :db/valueType])]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Saved failed. Missing key\(s\) for entity of type ':test': :test/hr, :test/record-date"
                            (sut/save! {:type    :test
                                        :test/bp 120N}))))))
