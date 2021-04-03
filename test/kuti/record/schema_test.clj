(ns kuti.record.schema-test
  (:require [kuti.record.core :as rec]
            [kuti.support.time :as time]
            [kuti.support.digest :refer [->uuid]]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.record.schema :as sut]
            [clojure.test :refer :all])
  (:import [java.lang IllegalArgumentException AssertionError]))

(use-fixtures :once
  record-fixtures/load-states
  time-fixtures/freeze-clock)

(def record {:crux.db/id (->uuid "3291d680-0d70-4940-914d-35413e261115")
             :updated-at @time/clock
             :record     "vinyl"
             :artist     "The Who"})

(def record-without-id {:city "Igatpuri"
                        :state "Maharashtra"
                        :country "India"
                        :population 1234})

(deftest save!
  (testing "requires a :type"
    (is (thrown-with-msg? java.lang.AssertionError
                          #":type key expected"
                          (sut/save! {:user/name "Vikram"}))))

  (testing "implied type must match :type"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Some keys did not match specified :type. :user/name, :address/street"
                          (sut/save! {:type :essay
                                      :user/name "Vikram"
                                      :address/street "Main St."}))))

  (testing "saves doc with correctly-typed keys"
    (let [_ (sut/add-type :essay [:essay/title :essay/bookmarked])
          _ (sut/add-schema :essay/title      :db.type/string)
          _ (sut/add-schema :essay/bookmarked :db.type/boolean)
          saved (sut/save! {:type             :essay
                            :essay/title      "Strength of the Record"
                            :essay/bookmarked true})
          found (rec/get (:crux.db/id saved))]
      (is (= {:updated-at       @time/clock
              :type             :essay
              :essay/title      "Strength of the Record"
              :essay/bookmarked true}
             (dissoc found :crux.db/id)))))

  (testing "rejects doc with missing keys"
    (let [_ (sut/add-type :test [:test/bp :test/hr :test/record-date])
          _ (sut/add-schema :test/bp          :db.type/bigint)
          _ (sut/add-schema :test/hr          :db.type/bigdec)
          _ (sut/add-schema :test/record-date :db.type/instant)]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Saved failed. Missing key\(s\) for entity of type ':test': :test/hr, :test/record-date"
                            (sut/save! {:type    :test
                                        :test/bp 120N}))))))

(deftest datatypes-for-save!
  (testing "handles doubles"
    (let [_ (sut/add-type :dub [:dub/dubdub])
          _ (sut/add-schema :dub/dubdub :db.type/double)]
      (is (= java.lang.Double
             (-> (sut/save! {:type       :dub
                             :dub/dubdub 1.0})
                 :dub/dubdub
                 class)))))

  (let [_ (sut/add-type :flt [:flt/width])
        _ (sut/add-schema :flt/width :db.type/float)]
    (testing "handles floats"
      (is (= java.lang.Float
           (-> (sut/save! {:type      :flt
                           :flt/width 1.0})
               :flt/width
               class))))

    (testing "demotion from double causes precision loss"
      (is (= (float 1.0123457)
           (-> (sut/save! {:type      :flt
                           :flt/width 1.0123456789012345})
               :flt/width)))))

  (let [_ (sut/add-type :ins [:ins/at])
        _ (sut/add-schema :ins/at :db.type/inst)]
    (testing "handles java.time.Instant"
      (is (= java.time.Instant
             (-> (sut/save! {:type   :ins
                             :ins/at (time/now)})
                 :ins/at
                 class))))

    (testing "forces #inst (java.util.Date) to java.time.Instant"
      (is (= java.time.Instant
             (-> (sut/save! {:type   :ins
                             :ins/at (java.util.Date.)})
                 :ins/at
                 class))))))
