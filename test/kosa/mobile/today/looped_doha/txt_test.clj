(ns kosa.mobile.today.looped-doha.txt-test
  (:require [kosa.mobile.today.looped-doha.txt :as sut]
            [kosa.mobile.today.looped.txt :as looped]
            [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kuti.support.debugging :refer :all])
  (:import [java.net URI]))

(def i (sut/->DohaIngester))

(deftest simple-parsing-txt-file
  (testing "parses out one day without whitespace or separators"
    (let [f (file-fixtures/file "doha_raw.txt")
          txt (slurp f)]
      (is (= {:looped-doha/doha "Dharama Dharama to saba kaheṅ, \npara samajhe nā koya. \nŚuddha citta kā ācaraṇa, \nśuddha Dharama hai soya. \n\nDharama Dharama to saba kaheṅ, \nDharama nā samajhe koya. \nNirmala mana kā ācaraṇa, \nSatya Dharama hai soya."
              :looped-doha/original-audio-url (URI. "http://download.pariyatti.org/dohas/008_Doha.mp3")
              :looped-doha/translations [["eng" "Everyone talks about Dhamma \nbut no one understands it. \nPracticing purity of mind— \nthis is true Dhamma. \n\nEveryone talks about Dhamma \nbut no one understands it. \nPracticing purity of mind— \nthis is true Dhamma.\n\n–S.N. Goenka"]]}
             (first (looped/parse i txt "eng")))))))

#_(deftest illegal-characters
  (testing "parses an entry with illegal unicode characters"
    (let [f (file-fixtures/file "words_of_buddha_es_illegal_characters.txt")
          txt (slurp f)]
      (is (= (URI. "http://tipitaka.org/romn/cscd/s0103m.mul7.xml#para273")
             (-> (looped/parse i txt "spa") first :looped-words-of-buddha/citepali-url)))))

  (testing "parses an entry with illegal characters in mp3 URI"
    (let [f (file-fixtures/file "words_of_buddha_zh_illegal_characters.txt")
          txt (slurp f)]
      (is (= (URI. "http://download.pariyatti.org/dwob/digha_nikaya_3_273_a.mp3")
             (-> (looped/parse i txt "zho-hant") first :looped-words-of-buddha/original-audio-url)))))

  (testing "parses an entry with horizontal whitespace (space) in the empty line"
   (let [f (file-fixtures/file "words_of_buddha_fr_extra_whitespace.txt")
         txt (slurp f)
         parsed (looped/parse i txt "fra")]
     (is (= (URI. "http://tipitaka.org/romn/cscd/s0505m.mul2.xml#para726")
            (-> parsed first :looped-words-of-buddha/citepali-url))))))

#_(deftest parsing-txt-file
  (testing "parses all days"
    (let [f (file-fixtures/file "words_of_buddha_raw.txt")
          txt (slurp f)]
      (is (= [#:looped-words-of-buddha
              {:words "Gahakāraka, diṭṭhosi!\nPuna gehaṃ na kāhasi.\nSabbā te phāsukā bhaggā gahakūṭaṃ visaṅkhataṃ.\nVisaṅkhāragataṃ cittaṃ;\ntaṇhānaṃ khayamajjhagā."
               :original-audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_11_154.mp3")
               :translations [["eng" "O house-builder, you are seen!\nYou will not build this house again.\nFor your rafters are broken and your ridgepole shattered.\nMy mind has reached the Unconditioned;\nI have attained the destruction of craving."]]
               :citepali "Dhammapada 11.154"
               :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul10.xml#para154")
               :citebook "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
               :citebook-url (URI. "https://store.pariyatti.org/The-Dhammapada-The-Buddhas-Path-of-Wisdom-Pocket-Edition_p_6305.html")}

              #:looped-words-of-buddha
              {:words "Sace bhāyatha dukkhassa, sace vo dukkhamappiyaṃ,\nmākattha pāpakaṃ kammaṃ, āvi vā yadi vā raho.\nSace ca pāpakaṃ kammaṃ, karissatha karotha vā,\nNa vo dukkhā pamutyatthi:\nupeccapi palāyataṃ."
               :original-audio-url (URI. "http://download.pariyatti.org/dwob/udana_5_44.mp3")
               :translations [["eng" "If you fear pain, if you dislike pain,\ndon't do an evil deed in open or secret.\nIf you're doing or will do an evil deed,\nyou won't escape pain:\nit will catch you even as you run away."]]
               :citepali "Udāna 5.44"
               :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0503m.mul4.xml#para44")
               :citebook "Translated from Pāli by Thanissaro Bhikkhu"
               :citebook-url (URI. "")}

              #:looped-words-of-buddha
              {:words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
               :original-audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_20_276.mp3")
               :translations [["eng" "You have to do your own work;\nEnlightened Ones will only show the way.\nThose who practise meditation\nwill free themselves from the chains of death."]]
               :citepali "Dhammapada 20.276"
               :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul19.xml#para276")
               :citebook "The Discourse Summaries by S.N. Goenka"
               :citebook-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")}]
             (looped/parse i txt "eng"))))))
