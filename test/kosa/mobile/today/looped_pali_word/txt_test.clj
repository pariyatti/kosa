(ns kosa.mobile.today.looped-pali-word.txt-test
  (:require [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.mobile.today.looped-pali-word.txt :as sut]
            [kosa.mobile.today.looped.txt :as looped]
            [kuti.support.debugging :refer :all]))

(def i (sut/->PaliIngester))

(deftest parsing-txt-file
  (testing "parses out text without whitespace or separators"
    (let [f (file-fixtures/file "pali_word_raw.txt")
          txt (slurp f)]
      (is (= [{:looped-pali-word/pali "vimutti"
               :looped-pali-word/translations [["eng" "freedom, release, deliverance, emancipation, liberation"]]}
              {:looped-pali-word/pali "kataññū"
               :looped-pali-word/translations [["eng" "kata + ññū = what is done + knowing, acknowledging what has been done (to, for one), grateful"]]}
              {:looped-pali-word/pali "tarati"
               :looped-pali-word/translations [["eng" "to cross [a river], to surmount, overcome [the great flood of life, desire, ignorance], to get to the other side, to cross over, as in crossing the ocean of suffering"]]}]
             (looped/parse i txt "eng"))))))
