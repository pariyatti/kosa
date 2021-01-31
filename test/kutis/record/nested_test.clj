(ns kutis.record.nested-test
  (:require [clojure.test :refer :all]
            [kutis.fixtures.record-fixtures :as record-fixtures]
            [kutis.record.nested :as sut]
            [kutis.record :as record]))

(use-fixtures :once record-fixtures/load-states)

(deftest collapse-one
  (testing "collapses a named field"
    (let [entity {:zig-attachment
                  {:crux.db/id "123" :filename "this-zig.txt"}
                  :zag-attachment
                  {:crux.db/id "456" :filename "this-zag.txt"}}
          collapsed (sut/collapse-one entity
                                        :zig-attachment)
          id (:zig-attachment-id collapsed)]
      (is (= "123" id)))))

(deftest collapse-all
  (let [doc {:name "List of plants"
             :tree-plant {:name "Pine" :crux.db/id "123"}
             :shrub-plant {:name "Heather" :crux.db/id "456"}}
        collapsed (sut/collapse-all doc "plant")]

    (testing "collapses all attachments"
      (is (= {:name "List of plants"
              :tree-plant-id "123"
              :shrub-plant-id "456"})))))

(deftest expand-one
  (let [tree {:crux.db/id "123" :name "Birch"}
        shrub {:crux.db/id "456" :name "Sage"}
        doc {:crux.db/id "999"
             :name "List of plants"
             :tree-plant-id "123"
             :shrub-plant-id "456"}
        _ (record/put tree [:name])
        _ (record/put shrub [:name])
        _ (record/put doc [:name :tree-plant-id :shrub-plant-id])]
    (is (= {:crux.db/id "999"
            :name "List of plants"
            :tree-plant tree
            :shrub-plant-id "456"}
           (sut/expand-one doc :tree-plant)))))

(deftest expand-all
  (let [tree {:crux.db/id "123" :name "Birch"}
        shrub {:crux.db/id "456" :name "Sage"}
        doc {:crux.db/id "999"
             :name "List of plants"
             :tree-plant-id "123"
             :shrub-plant-id "456"}
        _ (record/put tree [:name])
        _ (record/put shrub [:name])
        _ (record/put doc [:name :tree-plant-id :shrub-plant-id])]
    (sut/expand-all doc :tree-plant)))
