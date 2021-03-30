(ns kosa.mobile.today.pali-word.handler-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [kosa.config :as config]
            [kosa.mobile.today.pali-word.db :as db]
            [kosa.mobile.today.pali-word.handler :as pali-word-handler]
            [kosa.routes :as routes]
            [kutis.fixtures.record-fixtures :as record-fixtures]
            [kutis.support.digest :refer [uuid]]
            [kutis.support.time :as time]
            [kutis.fixtures.time-fixtures :as time-fixtures]))

(use-fixtures :once time-fixtures/freeze-clock)
(use-fixtures :each record-fixtures/load-states)

(defn pali-word
  "TODO: Should probably use the spec generators for this"
  [word translation]
  (let [audio {:url "/audio/path"}
        translations [{:crux.db/id (uuid)
                       :translation translation
                       :language "en"}]]
    {:crux.db/id (uuid)
     :updated-at @time/clock
     :published-at (time/now)
     :header "sticky header"
     :bookmarkable true
     :shareable true
     :card-type "pali_word"
     :pali word
     :audio audio
     :translations translations}))

(deftest pali-word-listing-operation
  (testing "Can list pali words in reverse chronological order"
    (let [word-1 (db/put (pali-word "word-1" "translation-1"))
          word-2 (db/put (pali-word "word-2" "translation-2"))]
      ;; (prn (clojure.data/diff word-2 (first (db/list))))
      (is (= [word-2 word-1]
             (db/list))))))

(deftest http-params->edn-document
  (testing "Zips languages and translations"
    (let [params {:card-type "pali_word", :bookmarkable "true", :shareable "true", :header "Pali Word",
                  :pali "rani", :language ["hi" "en" "cn"], :translation ["rani" "queen" "wx"], :submit "Save"}
          req {:params params
               :reitit.core/router routes/router}
          response (pali-word-handler/create req)
          uuid (-> response :headers (get "Location") (clojure.string/split #"\/") last)
          doc (db/get uuid)]
      (is (= [["hi" "rani"] ["en" "queen"] ["cn" "wx"]]
             (:translations doc)))))

  (testing "Saves params to db"
    (let [params {:card-type "pali_word", :bookmarkable "true", :shareable "true", :header "Pali Word",
                  :pali "rani", :language ["hi" "en" "cn"], :translation ["rani" "queen" "wx"], :submit "Save"}
          req {:params params
               :reitit.core/router routes/router}
          response (pali-word-handler/create req)
          uuid (-> response :headers (get "Location") (clojure.string/split #"\/") last)
          doc (db/get uuid)]
      ;; TODO: ignore datetimes and assert on the entire doc
      (is (= [["hi" "rani"] ["en" "queen"] ["cn" "wx"]]
             (:translations doc))))))
