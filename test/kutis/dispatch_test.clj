(ns kutis.dispatch-test
  (:require clojure.data
            [clojure.test :refer :all]
            [kutis.dispatch :as sut]
            [kutis.fixtures :as truck-handler]))

(defn wrap-spec-validation [_spec handler]
  (fn [request]
    (handler request)))

(def routes [["trucks" {:name ::trucks-index
                        :aliases [::trucks-create]
                        :get  truck-handler/index
                        :post (wrap-spec-validation :entity/truck-request truck-handler/create)}]
             ["trucks/new" {:name ::trucks-new
                             :get  truck-handler/new}]
             ["trucks/:id" {:name   ::trucks-show
                            :aliases [::trucks-update ::trucks-destroy]
                            :get    truck-handler/show
                            :put    truck-handler/update
                            :delete truck-handler/destroy}]
             ["trucks/:id/edit" {:name ::trucks-edit
                                 :get  truck-handler/edit}]])

(deftest macro-expansion
  (testing "splats the keyword everywhere"
    ;; TODO: `"trucks" => :post` needs to be ignored
    (prn (clojure.data/diff routes (sut/resources :trucks)))
    (is (= routes
           (sut/resources :trucks)))))
