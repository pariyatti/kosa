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

(defn looped-pali-word [pw]
  (merge {:looped-pali-word/pali "tiṇṇa"
          :looped-pali-word/bookmarkable true
          :looped-pali-word/shareable true
          :looped-pali-word/original-pali "tiṇṇa"
          :looped-pali-word/original-url (URI. "")
          :looped-pali-word/translations
          [["en" "gone through, overcome, one who has attained nibbāna"]]}
         pw))
