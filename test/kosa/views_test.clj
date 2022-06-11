(ns kosa.views-test
  (:require [kosa.views :as sut]
            [reitit.ring :as rr]
            [clojure.test :refer :all]))

(defn index-stub [_])
(defn create-stub [_])
(defn new-stub [_])
(defn show-stub [_])
(defn update-stub [_])
(defn destroy-stub [_])
(defn edit-stub [_])

(deftest reverse-paths
  (let [router7 (rr/router ["/" [["users" {:name    :kosa.views/users-index
                                           :aliases [:kosa.views/users-create]
                                           :get     index-stub
                                           :create  create-stub}]
                                 ["users/new" {:name :kosa.views/users-new
                                               :get new-stub}]
                                 ["users/:id" {:name    :kosa.views/users-show
                                               :aliases [:kosa.views/users-update
                                                         :kosa.views/users-destroy]
                                               :get     show-stub
                                               :put     update-stub
                                               :delete  destroy-stub}]
                                 ["users/:id/edit" {:name :kosa.views/users-edit
                                                    :get  edit-stub}]]]
                           {:conflicts nil})
        request {:reitit.core/router router7}]
    (testing "index path"
      (is (= "/users" (sut/index-path request :users))))
    (testing "create path"
      (is (= "/users" (sut/create-path request :users))))
    (testing "new path"
      (is (= "/users/new" (sut/new-path request :users))))
    (testing "show path"
      (is (= "/users/1234" (sut/show-path request :users {:xt/id "1234"}))))
    (testing "update path"
      (is (= "/users/1234" (sut/update-path request :users {:xt/id "1234"}))))
    (testing "destroy path"
      (is (= "/users/1234" (sut/destroy-path request :users {:xt/id "1234"}))))
    (testing "edit path"
      (is (= "/users/1234/edit" (sut/edit-path request :users {:xt/id "1234"}))))))
