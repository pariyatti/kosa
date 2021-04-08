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

(deftest fields-and-ids
  (testing "fields without ns"
    (is (= :namo-id
           (sut/field->id :namo))))

  (testing "namespaced fields"
    (is (= :some.ns/namo-id
           (sut/field->id :some.ns/namo))))

  (testing "ids without ns"
    (is (= :namo
           (sut/id->field :namo-id))))

  (testing "namespaced ids"
    (is (= :some.ns/namo
           (sut/id->field :some.ns/namo-id)))))

(deftest collapse-one
  (testing "collapses a named field"
    (let [entity {:zig-attachment
                  {:crux.db/id id1 :attm/filename "this-zig.txt"}
                  :zag-attachment
                  {:crux.db/id id2 :attm/filename "this-zag.txt"}}
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
              :tree/name "Birch"}
        shrub {:crux.db/id id2 :shrub/name "Sage"}
        doc {:crux.db/id id3
             :plants/name "List of plants"
             :plants/tree-plant-id id1
             :plants/shrub-plant-id id2}
        _ (record/put tree [:tree/name])
        _ (record/put shrub [:shrub/name])
        _ (record/put doc [:plants/name :plants/tree-plant-id :plants/shrub-plant-id])]
    (is (= {:crux.db/id id3
            :plants/name "List of plants"
            :plants/tree-plant tree
            :plants/shrub-plant-id id2}
           (sut/expand-one doc :plants/tree-plant-id)))))

(deftest expand-all
  (let [tree {:crux.db/id id1
              :tree/name "Birch"}
        shrub {:crux.db/id id2
               :shrub/name "Sage"}
        doc {:crux.db/id id3
             :plants/name "List of plants"
             :plants/tree-plant-id id1
             :plants/shrub-plant-id id2}
        _ (record/put tree [:tree/name])
        _ (record/put shrub [:shrub/name])
        _ (record/put doc [:plants/name :plants/tree-plant-id :plants/shrub-plant-id])]
    (is (= {:crux.db/id id3
            :plants/name "List of plants"
            :plants/tree-plant tree
            :plants/shrub-plant shrub}
           (sut/expand-all doc :plant)))))
