(ns kosa.fixtures.model-fixtures
  (:import [java.net URI]))

(defn pali-word [pw]
  (merge {:pali-word/pali "tiṇṇa"
          :pali-word/bookmarkable true
          :pali-word/shareable true
          :pali-word/original-pali "tiṇṇa"
          :pali-word/original-url (URI. "")
          :pali-word/translations
          [["en" "gone through, overcome, one who has attained nibbāna"]]}
         pw))
