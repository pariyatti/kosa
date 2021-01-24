(ns kutis.dispatch-test
  (:require [clojure.data]
            [clojure.test :refer :all]
            [kutis.dispatch :as sut]
            [kutis.fixtures.dispatch-fixtures :as green-truck-handler]
            [kutis.fixtures.dispatch-fixtures :as first-handler]
            [kutis.fixtures.dispatch-fixtures :as second-handler]))

(defn wrap-spec-validation [_spec handler]
  (fn [request]
    (handler request)))

(def expected [["green-trucks" {:name    ::green-trucks-index
                                :aliases [::green-trucks-create]
                                :get     green-truck-handler/index
                                :post    (wrap-spec-validation :entity/green-truck-request green-truck-handler/create)}]
               ["green-trucks/new" {:name ::green-trucks-new
                                    :get  green-truck-handler/new}]
               ["green-trucks/:id" {:name    ::green-trucks-show
                                    :aliases [::green-trucks-update ::green-trucks-destroy]
                                    :get     green-truck-handler/show
                                    :put     green-truck-handler/update
                                    :delete  green-truck-handler/destroy}]
               ["green-trucks/:id/edit" {:name ::green-trucks-edit
                                         :get  green-truck-handler/edit}]])

(defn- clip [s matcher]
  (when s
    (clojure.string/replace (str s) matcher "")))

(defn- clip-verbs [x]
  (-> x
      (update-in [1 :post] clip #"\$fn.*")
      (update-in [1 :get] clip #" .*\]")
      (update-in [1 :put] clip #" .*\]")
      (update-in [1 :delete] clip #" .*\]")))

(defn- clip-actions [x]
  (-> x
      (update-in [1 :get] clip #"@.*")
      (update-in [1 :put] clip #"@.*")
      (update-in [1 :delete] clip #"@.*")))

(defn- clip-fns [v]
  (->> v
       (map clip-verbs)
       (map clip-actions)))

(deftest macro-expansion
  (testing "splats the keyword everywhere"
    (let [clipped-expected (clip-fns expected)
          clipped-resources (clip-fns (sut/resources :green-trucks))]
      ;; for debugging:
      ;; (prn (clojure.data/diff clipped-expected clipped-resources))
      (is (= clipped-expected
             clipped-resources)))))

(deftest multiple-resources
  (testing "handles n-arity resources by name"
    (is (= 8 (count (sut/resources :firsts :seconds))))))
