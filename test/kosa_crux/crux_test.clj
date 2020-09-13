(ns kosa-crux.crux-test
  (:require [clojure.test :refer :all]
            [kosa-crux.crux :as crux]))

(def record {:crux.db/id :record-id
             :record     "vinyl"
             :artist     "The Who"})

(deftest crux-insert-operations
  (testing "Can insert and query records"
    (let [_ (crux/insert record)]
      (is (= record (crux/get :record-id))))))

(deftest crux-update-operations
  (testing "Can update records"
    (let [_ (crux/insert record)
          new-record (-> record
                         (assoc :artist "the kinks" :song "Lola"))]
      (crux/insert new-record)
      (is (= (crux/get :record-id)
             new-record)))))
