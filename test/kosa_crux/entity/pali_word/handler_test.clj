(ns kosa-crux.publisher.entity.pali-word.handler-test
  (:require [clojure.test :refer :all]
            [kosa-crux.fixtures :as fixtures]
            [kosa-crux.crux :as crux]
            [kosa-crux.publisher.entity.pali-word.handler :as handler]))

(use-fixtures :once fixtures/load-states)

(defn pali-word
  "Should probably use the spec generators for this"
  [word translation]
  (let [audio {:url "/audio/path"}
        translations [{:id (java.util.UUID/randomUUID)
                       :translation translation
                       :language "en"}]]
    {:id (java.util.UUID/randomUUID)
     :header "sticky header"
     :bookmarkable true
     :shareable true
     :type "pali_word"
     :pali word
     :published-at (java.util.Date.)
     :audio audio
     :translations translations}))

(deftest pali-word-listing-operation
  (testing "Can list pali words"
    (let [word-1 (crux/insert (-> (pali-word "word-1" "translation-1")
                                  (assoc :crux.db/id :word-1)))
          word-2 (crux/insert (-> (pali-word "word-2" "translation-2")
                                  (assoc :crux.db/id :word-1)))]
      (is (= [word-1 word-2] (handler/list))))))
