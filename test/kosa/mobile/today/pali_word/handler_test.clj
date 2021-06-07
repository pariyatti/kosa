(ns kosa.mobile.today.pali-word.handler-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [kosa.config :as config]
            [kosa.mobile.today.pali-word.db :as db]
            [kosa.mobile.today.pali-word.handler :as pali-word-handler]
            [kosa.routes :as routes]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.support.digest :refer [uuid]]
            [kuti.support.time :as time]
            [kuti.fixtures.time-fixtures :as time-fixtures])
  (:import [java.net URI]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(defn pali-word
  "TODO: Should probably use the spec generators for this"
  [word translation]
  (let [audio {:url "/audio/path"}
        translations [{:crux.db/id (uuid)
                       :translation translation
                       :language "en"}]]
    {:crux.db/id (uuid)
     :kuti/type :pali-word
     :pali-word/updated-at time-fixtures/win95
     :pali-word/published-at (time/now)
     :pali-word/pali word
     :pali-word/translations translations
     :pali-word/original-pali ""
     :pali-word/original-url (URI. "")}))

(deftest pali-word-listing-operation
  (testing "Can list pali words in reverse chronological order"
    (time/unfreeze-clock!)
    (let [word-1 (db/save! (pali-word "word-1" "translation-1"))
          word-2 (db/save! (pali-word "word-2" "translation-2"))]
      (is (= (map :pali-word/pali [word-2 word-1])
             (map :pali-word/pali (db/list)))))
    (time/freeze-clock! time-fixtures/win95)))

(deftest http-params->edn-document
  (testing "Zips languages and translations"
    (let [params {:type "pali_word",
                  :header "Pali Word",
                  :pali "rani",
                  :language ["hi" "en" "cn"],
                  :translation ["rani" "queen" "wx"],
                  :submit "Save"}
          req {:params params
               :reitit.core/router routes/router}
          response (pali-word-handler/create req)
          uuid (-> response :headers (get "Location") (clojure.string/split #"\/") last)
          doc (db/get uuid)]
      (is (= [["hi" "rani"] ["en" "queen"] ["cn" "wx"]]
             (:pali-word/translations doc)))))

  (testing "Saves params to db"
    (let [params {:type "pali_word",
                  :header "Pali Word",
                  :pali "rani",
                  :language ["hi" "en" "cn"],
                  :translation ["rani" "queen" "wx"],
                  :submit "Save"}
          req {:params params
               :reitit.core/router routes/router}
          response (pali-word-handler/create req)
          uuid (-> response :headers (get "Location") (clojure.string/split #"\/") last)
          doc (db/get uuid)]
      ;; TODO: ignore datetimes and assert on the entire doc
      (is (= [["hi" "rani"] ["en" "queen"] ["cn" "wx"]]
             (:pali-word/translations doc))))))
