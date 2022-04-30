(ns kosa.fixtures.model-fixtures
  (:require [kuti.support.digest :as digest])
  (:import [java.net URI]
           [java.util UUID]))

(defn pali-word [pw]
  (merge {:pali-word/pali "tiṇṇa"
          :pali-word/original-pali "tiṇṇa"
          :pali-word/original-url (URI. "")
          :pali-word/translations
          [["eng" "gone through, overcome, one who has attained nibbāna"]]}
         pw))

(defn looped-pali-word [pw]
  (merge {:looped-pali-word/pali "tiṇṇa"
          :looped-pali-word/original-pali "tiṇṇa"
          :looped-pali-word/original-url (URI. "")
          :looped-pali-word/translations
          [["eng" "gone through, overcome, one who has attained nibbāna"]]}
         pw))

(defn audio-attachment [aud]
  (merge {:kuti/type :attm,
          ;; :xt/id #uuid "da982712-4753-4939-a912-b582adc598c1"
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
         {:original-words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :original-url (URI. "")
          :words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :audio-attachment (audio-attachment {})
          :original-audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_20_276.mp3")
          :translations [["eng" "You have to do your own work;\nEnlightened Ones will only show the way.\nThose who practise meditation\nwill free themselves from the chains of death."]]
          :citepali "Dhammapada 20.276"
          :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul19.xml#para276")
          :citebook "The Discourse Summaries by S.N. Goenka"
          :citebook-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")}
         wob))

(defn looped-doha [doha]
  (merge #:looped-doha
         {:original-doha "Dasoṅ diśāṇa ke sabhī, \nprāṇī sukhiyā hoṅya. \nNirabhaya hoṅ, nirabaira hoṅ, \nsabhī nirāmaya hoṅya."
          :original-url (URI. "")
          :doha "Dasoṅ diśāṇa ke sabhī, \nprāṇī sukhiyā hoṅya. \nNirabhaya hoṅ, nirabaira hoṅ, \nsabhī nirāmaya hoṅya."
          :audio-attachment (audio-attachment {})
          :original-audio-url (URI. "http://download.pariyatti.org/dohas/117a_Doha.mp3")
          :translations [["eng" "In the ten directions, \nmay beings be happy, \nwithout fear or enmity; \nmay all be freed of ills. \n\n–S.N. Goenka"]]}
         doha))
