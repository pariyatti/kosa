(ns kosa.mobile.today.pali-word.txt-job-test
  (:require [kosa.mobile.today.pali-word.txt-job :as sut]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [clojure.test :refer :all]))

(deftest parsing-txt-file
  (testing "parses out text without whitespace or separators"
    (let [f (file-fixtures/file "pali_word_raw.txt")]
      (is (= [{:pali "vimutti"
               :translations [["en" "freedom, release, deliverance, emancipation, liberation"]]}
              {:pali "kataññū"
               :translations [["en" "kata + ññū = what is done + knowing, acknowledging what has been done (to, for one), grateful"]]}
              {:pali "tarati"
               :translations [["en" "to cross [a river], to surmount, overcome [the great flood of life, desire, ignorance], to get to the other side, to cross over, as in crossing the ocean of suffering"]]}]
             (sut/parse (slurp f) "en"))))))
