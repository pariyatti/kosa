(ns kutis.dispatch-test
  (:require [clojure.data]
            [clojure.test :refer :all]
            [kutis.dispatch :as sut]
            [kutis.fixtures.dispatch-fixtures :as truck-handler]))

(defn wrap-spec-validation [_spec handler]
  (fn [request]
    (handler request)))

(def expected [["trucks" {:name ::trucks-index
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
          clipped-resources (clip-fns (sut/resources :trucks))]
      ;; for debugging:
      ;; (prn (clojure.data/diff clipped-expected clipped-resources))
      (is (= clipped-expected
             clipped-resources)))))
