(ns kosa.publisher.entity.pali-word.handler-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [kutis.fixtures.record-fixtures :as fixtures]
            [kosa.config :as config]
            [kosa.publisher.entity.pali-word.db :as db]
            [kosa.publisher.entity.pali-word.handler :as pali-word-handler]
            [kosa.routes :as routes]))

(use-fixtures :each fixtures/load-states)

(defn pali-word
  "TODO: Should probably use the spec generators for this"
  [word translation]
  (let [audio {:url "/audio/path"}
        translations [{:id (java.util.UUID/randomUUID)
                       :translation translation
                       :language "en"}]]
    {:id (java.util.UUID/randomUUID)
     :published-at (java.util.Date.)
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
      (is (= [word-2 word-1] (db/list))))))

(deftest http-params->edn-document
  (testing "Zips languages and translations"
    (let [params {:card-type "pali_word", :bookmarkable "true", :shareable "true", :header "Pali Word",
                  :pali "rani", :language ["hi" "en" "cn"], :translation ["rani" "queen" "wx"], :submit "Save"}
          req {:params params
               :router routes/router}
          response (pali-word-handler/create req)
          uuid (-> response :headers (get "Location") (clojure.string/split #"\/") last)
          doc (db/get uuid)]
      (is (= [["hi" "rani"] ["en" "queen"] ["cn" "wx"]]
             (:translations doc)))))

  (testing "Saves params to db"
    (let [params {:card-type "pali_word", :bookmarkable "true", :shareable "true", :header "Pali Word",
                  :pali "rani", :language ["hi" "en" "cn"], :translation ["rani" "queen" "wx"], :submit "Save"}
          req {:params params
               :router routes/router}
          response (pali-word-handler/create req)
          uuid (-> response :headers (get "Location") (clojure.string/split #"\/") last)
          doc (db/get uuid)]
      ;; TODO: ignore datetimes and assert on the entire doc
      (is (= [["hi" "rani"] ["en" "queen"] ["cn" "wx"]]
             (:translations doc))))))
