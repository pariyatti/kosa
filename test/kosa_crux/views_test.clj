(ns kosa-crux.views-test
  (:require [clojure.test :refer :all]
            [reitit.ring :as rr]
            [kosa-crux.views :as sut]))

(defn index-stub [_])
(defn create-stub [_])
(defn new-stub [_])
(defn show-stub [_])
(defn update-stub [_])
(defn destroy-stub [_])
(defn edit-stub [_])

(deftest reverse-routing

  (let [router (rr/router ["/" [["entities" [["/users/:id" {:name    :kosa-crux.views/users-show
                                                            :aliases {:kosa-crux.views/users-destroy
                                                                      :kosa-crux.views/users-show}
                                                            :get     show-stub
                                                            :delete  destroy-stub}]]]]])
        request {:router router}]
    (testing "Finds route names"
      (is (= "/entities/users/123" (sut/path-for request :kosa-crux.views/users-show 123))))
    (testing "Finds aliased route names"
      (is (= "/entities/users/123" (sut/path-for request :kosa-crux.views/users-destroy 123))))
    (testing "Throws exception when named route can't be found"
      (is (thrown-with-msg? java.lang.Exception #"Named route ':this-route-will-not-be-found' cannot be found."
                            (sut/path-for request :this-route-will-not-be-found)))))

  (let [router-without-id (rr/router ["/" [["users" {:name    :kosa-crux.views/users-index
                                                     :aliases {:kosa-crux.views/users-create
                                                               :kosa-crux.views/users-index}
                                                     :get     index-stub
                                                     :create  create-stub}]]])
        request {:router router-without-id}]
    (testing "Finds route names without id"
      (is (= "/users" (sut/path-for request :kosa-crux.views/users-index))))
    (testing "Finds aliased route names without id"
      (is (= "/users" (sut/path-for request :kosa-crux.views/users-create)))))

  (let [router-without-alias (rr/router ["/" [["users" {:name :kosa-crux.views/users-index
                                                        :get  index-stub}]]])
        request {:router router-without-alias}]
    (testing "Finds routes without aliases"
      (is (= "/users" (sut/path-for request :kosa-crux.views/users-index)))))

  (let [router-with-multiple-aliases (rr/router ["/" [["users/:id" {:name    :kosa-crux.views/users-show
                                                                     :aliases {:kosa-crux.views/users-destroy
                                                                               :kosa-crux.views/users-show}
                                                                     :get     show-stub
                                                                     :delete  destroy-stub}]
                                                      ["users" {:name    :kosa-crux.views/users-index
                                                                :aliases {:kosa-crux.views/users-create
                                                                          :kosa-crux.views/users-index}
                                                                :get     index-stub
                                                                :create  create-stub}]]])
        request {:router router-with-multiple-aliases}]
    (testing "Finds alias from first route"
      (is (= "/users/123" (sut/path-for request :kosa-crux.views/users-destroy 123))))
    (testing "Finds alias from second route"
      (is (= "/users" (sut/path-for request :kosa-crux.views/users-create 123)))))

  (let [router7 (rr/router ["/" [["users" {:name    :kosa-crux.views/users-index
                                           :aliases {:kosa-crux.views/users-create
                                                     :kosa-crux.views/users-index}
                                           :get     index-stub
                                           :create  create-stub}]
                                 ["users/new" {:name :kosa-crux.views/users-new
                                               :get new-stub}]
                                 ["users/:id" {:name    :kosa-crux.views/users-show
                                               :aliases {:kosa-crux.views/users-update :kosa-crux.views/users-show
                                                         :kosa-crux.views/users-destroy :kosa-crux.views/users-show}
                                               :get     show-stub
                                               :put     update-stub
                                               :delete  destroy-stub}]
                                 ["users/:id/edit" {:name :kosa-crux.views/users-edit
                                                    :get  edit-stub}]]]
                           {:conflicts nil})
        request {:router router7}]
    (testing "index path"
      (is (= "/users" (sut/index-path request :users))))
    (testing "create path"
      (is (= "/users" (sut/create-path request :users))))
    (testing "new path"
      (is (= "/users/new" (sut/new-path request :users))))
    (testing "show path"
      (is (= "/users/1234" (sut/show-path request :users {:crux.db/id "1234"}))))
    (testing "update path"
      (is (= "/users/1234" (sut/update-path request :users {:crux.db/id "1234"}))))
    (testing "destroy path"
      (is (= "/users/1234" (sut/destroy-path request :users {:crux.db/id "1234"}))))
    (testing "edit path"
      (is (= "/users/1234/edit" (sut/edit-path request :users {:crux.db/id "1234"}))))))
