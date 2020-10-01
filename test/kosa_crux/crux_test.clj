(ns kosa-crux.crux-test
  (:require [clojure.test :refer :all]
            [crux.api]
            [kosa-crux.fixtures :as fixtures]
            [kosa-crux.crux :as crux]))

(use-fixtures :once fixtures/load-states)

(def record {:crux.db/id :record-id
             :record     "vinyl"
             :artist     "The Who"})

(deftest crux-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (crux/put* record)]
      (crux.api/await-tx crux/crux-node tx)
      (is (= record (crux/get :record-id)))))

  (testing "sync-put returns the record inserted"
    (let [inserted (crux/sync-put record [:record :artist])]
      (is (= (:record record) (:record inserted)))
      (is (= (:record record) (:record inserted))))))

(deftest crux-update-operations
  (testing "Can update raw datums"
    (let [_               (crux/put* record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (crux/put* new-record)]
      (crux.api/await-tx crux/crux-node inserted-record)
      (is (= (crux/get :record-id)
             new-record)))))
