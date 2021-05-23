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

(defn audio-attachment [aud]
  (merge {:kuti/type :attm,
          ;; :crux.db/id #uuid "da982712-4753-4939-a912-b582adc598c1"
          :attm/byte-size 183810,
          :attm/content-type "audio/mpeg",
          :attm/filename "dhammapada_20_276.mp3",
          :attm/metadata "",
          :attm/updated-at #time/instant "1995-08-24T00:00:00Z",
          :attm/checksum "11f875ec4588f6868d9a0e34cabf451c",
          :attm/service-name :disk,
          :attm/identified true,
          :attm/key "30ef5362250e3458c727d7209857cdff"}
         aud))

(defn looped-words-of-buddha [wob]
  (merge #:looped-words-of-buddha
         {:bookmarkable true
          :shareable true
          :original-words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :original-url (URI. "")
          :words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :audio-attachment (audio-attachment {})
          :audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_20_276.mp3")
          :translations [["en" "You have to do your own work;\nEnlightened Ones will only show the way.\nThose who practise meditation\nwill free themselves from the chains of death."]]
          :citation "Dhammapada 20.276"
          :citation-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul19.xml#para276")
          :store-title "The Discourse Summaries by S.N. Goenka"
          :store-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")}
         wob))
