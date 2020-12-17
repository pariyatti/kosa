(ns kosa-crux.views-test
  (:require [clojure.test :refer :all]
            [reitit.ring :as rr]
            [kosa-crux.views :as sut]))

(defn show-stub [_])
(defn destroy-stub [_])
(defn index-stub [_])
(defn create-stub [_])

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
      (is (= "/users" (sut/path-for request :kosa-crux.views/users-create 123))))))
