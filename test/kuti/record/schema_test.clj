(ns kuti.record.schema-test
  (:require [kuti.record.core :as rec]
            [kuti.support.time :as time]
            [kuti.support.digest :refer [->uuid]]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.record.schema :as sut]
            [clojure.test :refer :all])
  (:import [java.lang IllegalArgumentException AssertionError]
           [java.math BigDecimal BigInteger]
           [java.lang Boolean Double Float Long String]
           [java.util Date UUID]
           [java.time Instant]
           [clojure.lang Keyword Symbol PersistentVector]
           [java.net URI]))

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
                                      :crux.db/id 123
                                      :updated-at (time/now)
                                      :published-at (time/now)
                                      :user/name "Vikram"
                                      :address/street "Main St."}))))

  (testing "detects removed type"
    (sut/add-type :dog [:dog/name])
    (sut/add-schema :dog/name :db.type/string)
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert failed: Saved failed. Missing key\(s\) for entity of type ':dog': :dog/name"
                          (sut/save! {:type :dog :dog/breed "Shiba"})))
    (sut/remove-type :dog)
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert failed: Saved failed. DB is missing type for entity of type ':dog'."
                          (sut/save! {:type :dog :dog/breed "Shiba"}))))

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
                                        :test/bp 120N})))))

  (testing "rejects doc with :type missing in db"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Saved failed. DB is missing type for entity of type ':zig2'."
                          (sut/save! {:type       :zig2
                                      :zig2/attr1 "I bet someone forgot to migrate."})))))

(deftest datatypes-for-save!
  (testing "handles BigDecimals"
    (let [_ (sut/add-type :count [:count/dec])
          _ (sut/add-schema :count/dec :db.type/bigdec)]
      (is (= java.math.BigDecimal
             (-> (sut/save! {:type      :count
                             :count/dec 1.0M})
                 :count/dec
                 class)))))

  (testing "handles BigIntegers"
    (let [_ (sut/add-type :many [:many/int])
          _ (sut/add-schema :many/int :db.type/bigint)]
      (is (= java.math.BigInteger
             (-> (sut/save! {:type      :many
                             :many/int 7N})
                 :many/int
                 class)))))

  (testing "handles booleans"
    (let [_ (sut/add-type :site [:site/read])
          _ (sut/add-schema :site/read :db.type/boolean)]
      (is (= java.lang.Boolean
             (-> (sut/save! {:type      :site
                             :site/read false})
                 :site/read
                 class)))))

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

  (testing "handles java.util.Date"
    (let [_ (sut/add-type :java7 [:java7/yuck-date])
          _ (sut/add-schema :java7/yuck-date :db.type/instant)]
      (is (= java.util.Date
             (-> (sut/save! {:type            :java7
                             :java7/yuck-date (java.util.Date.)})
                 :java7/yuck-date
                 class)))))

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
                 class)))))

  (testing "handles keywords"
    (let [_ (sut/add-type :kw [:kw/k])
          _ (sut/add-schema :kw/k :db.type/keyword)]
      (is (= clojure.lang.Keyword
             (-> (sut/save! {:type :kw
                             :kw/k :i-am-a-keyword})
                 :kw/k
                 class)))))

  (let [_ (sut/add-type :lumbi [:lumbi/n])
        _ (sut/add-schema :lumbi/n :db.type/long)]
    (testing "handles longs"
      (is (= java.lang.Long
             (-> (sut/save! {:type    :lumbi
                             :lumbi/n 1234})
                 :lumbi/n
                 class))))

    (testing "attempting demotion from BigInteger throws an exception"
      (is (thrown-with-msg? java.lang.IllegalArgumentException
                            #"Value out of range for long: 18446744073709551614"
                            (sut/save! {:type    :lumbi
                                        :lumbi/n (+ (biginteger Long/MAX_VALUE)
                                                    (biginteger Long/MAX_VALUE))})))))

  ;; NOTE: `:db:type/ref` is intentionally elided.
  ;;       Crux refs are implicit and can be of any type.

  (testing "handles strings"
    (let [_ (sut/add-type :book [:book/author])
          _ (sut/add-schema :book/author :db.type/string)]
      (is (= java.lang.String
             (-> (sut/save! {:type        :book
                             :book/author "Paul Fleischman"})
                 :book/author
                 class)))))

  (testing "handles symbols"
    (let [_ (sut/add-type :sym [:sym/s])
          _ (sut/add-schema :sym/s :db.type/symbol)]
      (is (= clojure.lang.Symbol
             (-> (sut/save! {:type  :sym
                             :sym/s 'i-am-symbol})
                 :sym/s
                 class)))))

  (testing "handles tuples (vectors only)"
    (let [_ (sut/add-type :tup [:tup/v])
          _ (sut/add-schema :tup/v :db.type/tuple)]
      (is (= clojure.lang.PersistentVector
             (-> (sut/save! {:type  :tup
                             :tup/v ["a" "b" "c"]})
                 :tup/v
                 class)))))

  (testing "handles UUIDs"
    (let [_ (sut/add-type :identity [:identity/refid])
          _ (sut/add-schema :identity/refid :db.type/uuid)]
      (is (= java.util.UUID
             (-> (sut/save! {:type           :identity
                             :identity/refid (java.util.UUID/randomUUID)})
                 :identity/refid
                 class)))))

  (testing "handles URIs"
    (let [_ (sut/add-type :page [:page/url])
          _ (sut/add-schema :page/url :db.type/uri)]
      (is (= java.net.URI
             (-> (sut/save! {:type  :page
                             :page/url (java.net.URI. "https://pariyatti.org")})
                 :page/url
                 class)))))

  (testing "handles byte arrays"
    (let [_ (sut/add-type :interop [:interop/arr])
          _ (sut/add-schema :interop/arr :db.type/bytes)]
      (is (= (Class/forName "[B")
             (-> (sut/save! {:type  :interop
                             :interop/arr (byte-array [1 2 3])})
                 :interop/arr
                 class))))))
