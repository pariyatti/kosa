(ns kutis.record-test
  (:require [clojure.test :refer :all]
            [crux.api]
            [kosa.fixtures :as fixtures]
            [kutis.record :as db]))

(use-fixtures :once fixtures/load-states)

(def record {:crux.db/id :record-id
             :record     "vinyl"
             :artist     "The Who"})

(def record-without-id {:city "Igatpuri"
                        :state "Maharashtra"
                        :country "India"
                        :population 1234})

(deftest db-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (db/put-async* record)]
      (crux.api/await-tx db/crux-node tx)
      (is (= record (db/get :record-id)))))

  (testing "put returns the record inserted"
    (let [inserted (db/put record [:record :artist])]
      (is (= (:record record) (:record inserted)))
      (is (= (:artist record) (:artist inserted)))))

  (testing "put generates a new id"
    (let [inserted (db/put record-without-id [:country :state :city :population])]
      (is (= record-without-id (dissoc inserted :crux.db/id)))
      (is (not (nil? (:crux.db/id inserted)))))))

(deftest db-update-operations
  (testing "Can update raw datums"
    (let [_               (db/put-async* record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (db/put-async* new-record)]
      (crux.api/await-tx db/crux-node inserted-record)
      (is (= (db/get :record-id)
             new-record))))

  (testing "put overwrites an existing record"
    (let [required-fields [:country :state :city :population]
          created (db/put record-without-id required-fields)
          updated (db/put (update created :population #(+ % 5555)) required-fields)]
      (is (= 1234 (:population created)))
      (is (= 6789 (:population updated)))
      (is (= (:crux.db/id created) (:crux.db/id updated))))))
