(ns kosa-crux.publisher.entity.pali-word.handler-test
  (:require [clojure.test :refer :all]
            [kosa-crux.fixtures :as fixtures]
            [kosa-crux.config :as config]
            [kosa-crux.publisher.entity.pali-word.db :as db]
            [kosa-crux.publisher.entity.pali-word.handler :as handler]))

(use-fixtures :each fixtures/load-states)

(defn pali-word
  "TODO: Should probably use the spec generators for this"
  [word translation]
  (let [audio {:url "/audio/path"}
        translations [{:id (java.util.UUID/randomUUID)
                       :translation translation
                       :language "en"}]]
    {:id (java.util.UUID/randomUUID)
     :header "sticky header"
     :bookmarkable true
     :shareable true
     :card-type "pali_word"
     :pali word
     :published-at (java.util.Date.)
     :audio audio
     :translations translations}))

(deftest pali-word-listing-operation
  (testing "Can list pali words"
    (let [word-1 (db/sync-put (pali-word "word-1" "translation-1"))
          word-2 (db/sync-put (pali-word "word-2" "translation-2"))]
      (is (= [word-2 word-1] (db/list))))))
