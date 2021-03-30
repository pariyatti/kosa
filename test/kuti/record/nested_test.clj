(ns kuti.record.nested-test
  (:require [clojure.test :refer :all]
            [clojure.data]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.record.nested :as sut]
            [kuti.record :as record]
            [kuti.support.digest :refer [uuid]]
            [kuti.support.time :as time]
            [kuti.fixtures.time-fixtures :as time-fixtures]))

(use-fixtures :once
  record-fixtures/load-states
  time-fixtures/freeze-clock)

(def id1 (uuid))
(def id2 (uuid))
(def id3 (uuid))

(deftest collapse-one
  (testing "collapses a named field"
    (let [entity {:zig-attachment
                  {:crux.db/id id1 :filename "this-zig.txt"}
                  :zag-attachment
                  {:crux.db/id id2 :filename "this-zag.txt"}}
          collapsed (sut/collapse-one entity
                                        :zig-attachment)
          id (:zig-attachment-id collapsed)]
      (is (= id1 id)))))

(deftest collapse-all
  (let [doc {:name "List of plants"
             :tree-plant {:name "Pine" :crux.db/id id1}
             :shrub-plant {:name "Heather" :crux.db/id id2}}
        collapsed (sut/collapse-all doc "plant")]

    (testing "collapses all attachments"
      (is (= collapsed
             {:name "List of plants"
              :tree-plant-id id1
              :shrub-plant-id id2})))))

(deftest expand-one
  (let [tree {:crux.db/id id1
              :updated-at @time/clock
              :name "Birch"}
        shrub {:crux.db/id id2 :name "Sage"}
        doc {:crux.db/id id3
             :name "List of plants"
             :tree-plant-id id1
             :shrub-plant-id id2}
        _ (record/put tree [:name])
        _ (record/put shrub [:name])
        _ (record/put doc [:name :tree-plant-id :shrub-plant-id])]
    (is (= {:crux.db/id id3
            :name "List of plants"
            :tree-plant tree
            :shrub-plant-id id2}
           (sut/expand-one doc :tree-plant-id)))))

(deftest expand-all
  (let [tree {:crux.db/id id1
              :updated-at @time/clock
              :name "Birch"}
        shrub {:crux.db/id id2
               :updated-at @time/clock
               :name "Sage"}
        doc {:crux.db/id id3
             :name "List of plants"
             :tree-plant-id id1
             :shrub-plant-id id2}
        _ (record/put tree [:name])
        _ (record/put shrub [:name])
        _ (record/put doc [:name :tree-plant-id :shrub-plant-id])]
    (is (= {:crux.db/id id3
            :name "List of plants"
            :tree-plant tree
            :shrub-plant shrub}
           (sut/expand-all doc :plant)))))
