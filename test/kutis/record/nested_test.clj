(ns kutis.record.nested-test
  (:require [clojure.test :refer :all]
            [kutis.record.nested :as sut]))

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
