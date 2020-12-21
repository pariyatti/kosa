(ns kosa.crux-test
  (:require [clojure.test :refer :all]
            crux.api
            [kosa.crux :as crux]
            [kosa.fixtures :as fixtures]))

(use-fixtures :once fixtures/load-states)

(def record {:crux.db/id :record-id
             :record     "vinyl"
             :artist     "The Who"})

(def record-without-id {:city "Igatpuri"
                        :state "Maharashtra"
                        :country "India"
                        :population 1234})

(deftest crux-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (crux/put-async* record)]
      (crux.api/await-tx crux/crux-node tx)
      (is (= record (crux/get :record-id)))))

  (testing "put returns the record inserted"
    (let [inserted (crux/put record [:record :artist])]
      (is (= (:record record) (:record inserted)))
      (is (= (:artist record) (:artist inserted)))))

  (testing "put generates a new id"
    (let [inserted (crux/put record-without-id [:country :state :city :population])]
      (is (= record-without-id (dissoc inserted :crux.db/id)))
      (is (not (nil? (:crux.db/id inserted)))))))

(deftest crux-update-operations
  (testing "Can update raw datums"
    (let [_               (crux/put-async* record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (crux/put-async* new-record)]
      (crux.api/await-tx crux/crux-node inserted-record)
      (is (= (crux/get :record-id)
             new-record))))

  (testing "put overwrites an existing record"
    (let [required-fields [:country :state :city :population]
          created (crux/put record-without-id required-fields)
          updated (crux/put (update created :population #(+ % 5555)) required-fields)]
      (is (= 1234 (:population created)))
      (is (= 6789 (:population updated)))
      (is (= (:crux.db/id created) (:crux.db/id updated))))))
