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
    (let [_ (rec/put {:db.entity/type :essay
                      :db.entity/attrs [:essay/title :essay/bookmarked]}
                     [:db.entity/type :db.entity/attrs])
          _ (rec/put {:db/ident     :essay/title
                      :db/valueType :db.type/string}
                     [:db/ident :db/valueType])
          _ (rec/put {:db/ident     :essay/bookmarked
                      :db/valueType :db.type/boolean}
                     [:db/ident :db/valueType])
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
    (let [_ (rec/put {:db.entity/type  :test
                      :db.entity/attrs [:test/bp :test/hr :test/record-date]}
                     [:db.entity/type :db.entity/attrs])
          _ (rec/put {:db/ident     :test/bp
                      :db/valueType :db.type/bigint}
                     [:db/ident :db/valueType])
          _ (rec/put {:db/ident     :test/hr
                      :db/valueType :db.type/bigdec}
                     [:db/ident :db/valueType])
          _ (rec/put {:db/ident     :test/record-date
                      :db/valueType :db.type/instant}
                     [:db/ident :db/valueType])]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Saved failed. Missing key\(s\) for entity of type ':test': :test/hr, :test/record-date"
                            (sut/save! {:type    :test
                                        :test/bp 120N})))))

  (testing "handles doubles"
    (let [_ (rec/put {:db.entity/type  :dub
                      :db.entity/attrs [:dub/dubdub]}
                     [:db.entity/type :db.entity/attrs])
          _ (rec/put {:db/ident     :dub/dubdub
                      :db/valueType :db.type/double}
                     [:db/ident :db/valueType])]
      (is (= java.lang.Double
             (-> (sut/save! {:type       :dub
                             :dub/dubdub 1.0})
                 :dub/dubdub
                 class))))))
