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
  (testing "Can insert and query records"
    (let [inserted-record (crux/insert record)]
      (crux.api/await-tx crux/crux-node inserted-record)
      (is (= record (crux/get :record-id))))))

(deftest crux-update-operations
  (testing "Can update records"
    (let [_               (crux/insert record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (crux/insert new-record)]
      (crux.api/await-tx crux/crux-node inserted-record)
      (is (= (crux/get :record-id)
             new-record)))))
