(ns kutis.search-test
  (:require [clojure.test :refer :all]
            [kutis.search :as sut]))

;; #"-|_|~|=|\$|\{|\}|\.|\[|\]|\+"

(deftest tagging-searchables
  (testing "replaces special characters with space"
    (let [entity (sut/tag-searchables {:some "entity"}
                                      "a-filename_with.some$special+chars.txt")]
      (is (= "a filename with some special chars txt a-filename_with.some$special+chars.txt"
             (:searchables entity)))))

  (testing "tagging is additive"
    (let [entity (sut/tag-searchables {:some "entity"
                                       :searchables "from some other time before"}
                                      "new-file.txt")]
      (is (= "from some other time before new file txt new-file.txt"
             (:searchables entity)))))

  (testing "tagging does not repeat"
    (let [repeated "new-file.txt"
          entity1 (sut/tag-searchables {:some "entity"} repeated)
          entity2 (sut/tag-searchables entity1 repeated)]
      (is (= "new file txt new-file.txt"
             (:searchables entity2))))))
