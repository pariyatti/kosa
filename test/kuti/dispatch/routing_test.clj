(ns kuti.dispatch.routing-test
  (:require [clojure.test :refer :all]
            [kuti.dispatch.routing :as sut]
            [reitit.ring :as rr]))

(defn index-stub [_])
(defn create-stub [_])
(defn new-stub [_])
(defn show-stub [_])
(defn update-stub [_])
(defn destroy-stub [_])
(defn edit-stub [_])

(deftest reverse-routing

  (let [router (rr/router ["/" [["entities" [["/users/:id" {:name    :kosa.views/users-show
                                                            :aliases [:kosa.views/users-destroy]
                                                            :get     show-stub
                                                            :delete  destroy-stub}]]]]])
        request {:reitit.core/router router}]
    (testing "Finds route names"
      (is (= "/entities/users/123" (sut/path-for request :kosa.views/users-show 123))))
    (testing "Finds aliased route names"
      (is (= "/entities/users/123" (sut/path-for request :kosa.views/users-destroy 123))))
    (testing "Throws exception when named route can't be found"
      (is (thrown-with-msg? java.lang.Exception #"Named route ':this-route-will-not-be-found' cannot be found."
                            (sut/path-for request :this-route-will-not-be-found)))))

  (let [router-without-id (rr/router ["/" [["users" {:name    :kosa.views/users-index
                                                     :aliases [:kosa.views/users-create]
                                                     :get     index-stub
                                                     :create  create-stub}]]])
        request {:reitit.core/router router-without-id}]
    (testing "Finds route names without id"
      (is (= "/users" (sut/path-for request :kosa.views/users-index))))
    (testing "Finds aliased route names without id"
      (is (= "/users" (sut/path-for request :kosa.views/users-create)))))

  (let [router-without-alias (rr/router ["/" [["users" {:name :kosa.views/users-index
                                                        :get  index-stub}]]])
        request {:reitit.core/router router-without-alias}]
    (testing "Finds routes without aliases"
      (is (= "/users" (sut/path-for request :kosa.views/users-index)))))

  (let [router-with-multiple-aliases (rr/router ["/" [["users/:id" {:name    :kosa.views/users-show
                                                                    :aliases [:kosa.views/users-destroy]
                                                                    :get     show-stub
                                                                    :delete  destroy-stub}]
                                                      ["users" {:name    :kosa.views/users-index
                                                                :aliases [:kosa.views/users-create]
                                                                :get     index-stub
                                                                :create  create-stub}]]])
        request {:reitit.core/router router-with-multiple-aliases}]
    (testing "Finds alias from first route"
      (is (= "/users/123" (sut/path-for request :kosa.views/users-destroy 123))))
    (testing "Finds alias from second route"
      (is (= "/users" (sut/path-for request :kosa.views/users-create 123)))))

  (let [router-with-colliding-aliases (rr/router [["/thing/:id" {:name    :kosa.views/thing-show
                                                                 :aliases [:kosa.views/thing-destroy]
                                                                 :get     show-stub
                                                                 :delete  destroy-stub}]
                                                  ["/synonym/:id" {:name    :kosa.views/synonym-show
                                                                   :aliases [:kosa.views/thing-destroy]
                                                                   :get     show-stub
                                                                   :delete  destroy-stub}]])
        request {:reitit.core/router router-with-colliding-aliases}]

    (testing "Barfs (at runtime, sorry) if duplicate aliases are detected"
      (is (thrown-with-msg? java.lang.Exception #"Alias ':kosa.views/thing-destroy' is colliding."
                            (sut/path-for request :kosa.views/thing-destroy 123))))))

(deftest urls
  (let [router (rr/router ["/" [["users" {:name :kosa.views/users-index
                                          :get  index-stub}]]])
        request {:reitit.core/router router
                 :server-name "kosa.pariyatti.app"
                 :server-port 443
                 :scheme :https
                 :protocol "HTTP/2"}]
    ;; based on ring spec: https://github.com/ring-clojure/ring/blob/master/SPEC
    (testing "re-assembles full https URLs"
      (is (= "https://kosa.pariyatti.app/users"
             (sut/url-for request :kosa.views/users-index)))))

  (let [router (rr/router ["/" [["users" {:name :kosa.views/users-index
                                          :get  index-stub}]]])
        request {:reitit.core/router router
                 :server-name "kosa-sandbox.pariyatti.app"
                 :server-port 80
                 :scheme :http
                 :protocol "HTTP/1.1"}]
    ;; based on ring spec: https://github.com/ring-clojure/ring/blob/master/SPEC
    (testing "re-assembles full http URLs"
      (is (= "http://kosa-sandbox.pariyatti.app/users"
             (sut/url-for request :kosa.views/users-index)))))

  (let [router (rr/router ["/" [["users" {:name :kosa.views/users-index
                                          :get  index-stub}]]])
        request {:reitit.core/router router
                 :server-name "localhost"
                 :server-port 8888
                 :scheme :http
                 :protocol "HTTP/1.1"}]
    ;; based on ring spec: https://github.com/ring-clojure/ring/blob/master/SPEC
    (testing "re-assembles full http URLs"
      (is (= "http://localhost:8888/users"
             (sut/url-for request :kosa.views/users-index))))))
