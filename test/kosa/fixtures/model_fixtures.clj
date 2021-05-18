(ns kosa.fixtures.model-fixtures
  (:require [kuti.support.digest :as digest])
  (:import [java.net URI]
           [java.util UUID]))

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

(defn looped-words-of-buddha [wob]
  (merge #:looped-words-of-buddha
         {:original-words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :original-url (URI. "")
          :words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :audio-attm-id (digest/null-uuid)
          :audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_20_276.mp3")
          :translations [["en" "You have to do your own work;\nEnlightened Ones will only show the way.\nThose who practise meditation\nwill free themselves from the chains of death."]]
          :citation "Dhammapada 20.276"
          :citation-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul19.xml#para276")
          :store-title "The Discourse Summaries by S.N. Goenka"
          :store-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")}
         wob))
