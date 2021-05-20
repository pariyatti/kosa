(ns kosa.mobile.today.looped-words-of-buddha.txt-test
  (:require [kosa.mobile.today.looped-words-of-buddha.txt :as sut]
            [kosa.mobile.today.looped-words-of-buddha.db :as db]
            [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.support.time :as time]
            [kuti.support.debugging :refer :all])
  (:import [java.net URI]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest simple-parsing-txt-file
  (testing "parses out one day without whitespace or separators"
    (let [f (file-fixtures/file "words_of_buddha_raw.txt")
          txt (slurp f)]
      (is (= {:looped-words-of-buddha/words "Gahakāraka, diṭṭhosi!\nPuna gehaṃ na kāhasi.\nSabbā te phāsukā bhaggā gahakūṭaṃ visaṅkhataṃ.\nVisaṅkhāragataṃ cittaṃ;\ntaṇhānaṃ khayamajjhagā."
              :looped-words-of-buddha/audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_11_154.mp3")
              :looped-words-of-buddha/translations [["en" "O house-builder, you are seen!\nYou will not build this house again.\nFor your rafters are broken and your ridgepole shattered.\nMy mind has reached the Unconditioned;\nI have attained the destruction of craving."]]
              :looped-words-of-buddha/citation "Dhammapada 11.154"
              :looped-words-of-buddha/citation-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul10.xml#para154")
              :looped-words-of-buddha/store-title "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
              :looped-words-of-buddha/store-url (URI. "https://store.pariyatti.org/The-Dhammapada-The-Buddhas-Path-of-Wisdom-Pocket-Edition_p_6305.html")}
             (first (sut/parse txt "en")))))))

(deftest parsing-txt-file
  (testing "parses all days"
    (let [f (file-fixtures/file "words_of_buddha_raw.txt")
          txt (slurp f)]
      (is (= [#:looped-words-of-buddha
              {:words "Gahakāraka, diṭṭhosi!\nPuna gehaṃ na kāhasi.\nSabbā te phāsukā bhaggā gahakūṭaṃ visaṅkhataṃ.\nVisaṅkhāragataṃ cittaṃ;\ntaṇhānaṃ khayamajjhagā."
               :audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_11_154.mp3")
               :translations [["en" "O house-builder, you are seen!\nYou will not build this house again.\nFor your rafters are broken and your ridgepole shattered.\nMy mind has reached the Unconditioned;\nI have attained the destruction of craving."]]
               :citation "Dhammapada 11.154"
               :citation-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul10.xml#para154")
               :store-title "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
               :store-url (URI. "https://store.pariyatti.org/The-Dhammapada-The-Buddhas-Path-of-Wisdom-Pocket-Edition_p_6305.html")}

              #:looped-words-of-buddha
              {:words "Sace bhāyatha dukkhassa, sace vo dukkhamappiyaṃ,\nmākattha pāpakaṃ kammaṃ, āvi vā yadi vā raho.\nSace ca pāpakaṃ kammaṃ, karissatha karotha vā,\nNa vo dukkhā pamutyatthi:\nupeccapi palāyataṃ."
               :audio-url (URI. "http://download.pariyatti.org/dwob/udana_5_44.mp3")
               :translations [["en" "If you fear pain, if you dislike pain,\ndon't do an evil deed in open or secret.\nIf you're doing or will do an evil deed,\nyou won't escape pain:\nit will catch you even as you run away."]]
               :citation "Udāna 5.44"
               :citation-url (URI. "http://tipitaka.org/romn/cscd/s0503m.mul4.xml#para44")
               :store-title "Translated from Pāli by Thanissaro Bhikkhu"
               :store-url (URI. "")}

              #:looped-words-of-buddha
              {:words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
               :audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_20_276.mp3")
               :translations [["en" "You have to do your own work;\nEnlightened Ones will only show the way.\nThose who practise meditation\nwill free themselves from the chains of death."]]
               :citation "Dhammapada 20.276"
               :citation-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul19.xml#para276")
               :store-title "The Discourse Summaries by S.N. Goenka"
               :store-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")}]
             (sut/parse txt "en"))))))

(deftest ingesting-txt-file
  (testing "inserts entries into db"
    (let [f (file-fixtures/file "words_of_buddha_raw.txt")]
      (sut/ingest f "en")
      (is (= 3 (count (db/list)))))))

(deftest merging-entities
  (testing "ignore identical entities"
    (db/save! (model/looped-words-of-buddha
               {:looped-words-of-buddha/words "Manopubbaṅgamā dhammā,"
                :looped-words-of-buddha/translations [["en" "Mind precedes all phenomena,"]]
                :looped-words-of-buddha/published-at (time/parse "2008-01-01")}))
    (sut/db-insert! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "Manopubbaṅgamā dhammā,"
                     :looped-words-of-buddha/translations [["en" "Mind precedes all phenomena,"]]
                     :looped-words-of-buddha/published-at (time/parse "2012-01-01")}))
    (let [mano (db/q :looped-words-of-buddha/words "Manopubbaṅgamā dhammā,")]
      (is (= 1 (count mano)))
      (is (= (time/parse "2008-01-01")
             (-> mano first :looped-words-of-buddha/published-at)))))

  (testing "merge additional languages if merged is not identical"
    (db/save! (model/looped-words-of-buddha
               {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                :looped-words-of-buddha/translations [["en" "Speak not harshly to anyone,"]
                                                      ["hi" "किसी से कटुता से न बोलें,"]]
                :looped-words-of-buddha/published-at (time/parse "2008-01-01")}))
    (sut/db-insert! (model/looped-words-of-buddha
                    {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                     :looped-words-of-buddha/translations [["fr" "Ne parlez pas durement à qui que ce soit,"]
                                                           ["es" "No hables agresivamente a nadie;"]]
                     :looped-words-of-buddha/published-at (time/parse "2012-01-01")}))
    (let [voca (db/q :looped-words-of-buddha/words "Māvoca pharusaṃ kañci,")]
      (is (= 1 (count  voca)))
      (is (= [["en" "Speak not harshly to anyone,"]
              ["hi" "किसी से कटुता से न बोलें,"]
              ["fr" "Ne parlez pas durement à qui que ce soit,"]
              ["es" "No hables agresivamente a nadie;"]]
             (-> voca first :looped-words-of-buddha/translations))))))

(deftest indexing
  (testing "index auto-increments"
    (sut/db-insert! (model/looped-words-of-buddha
                     {:looped-words-of-buddha/words "Manopubbaṅgamā dhammā,"
                      :looped-words-of-buddha/translations [["en" "Mind precedes all phenomena,"]]}))
    (sut/db-insert! (model/looped-words-of-buddha
                     {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                      :looped-words-of-buddha/translations [["en" "Speak not harshly to anyone,"]]}))
    (let [mano (db/q :looped-words-of-buddha/words "Manopubbaṅgamā dhammā,")
          voca (db/q :looped-words-of-buddha/words "Māvoca pharusaṃ kañci,")]
      (is (= 1 (- (-> voca first :looped-words-of-buddha/index)
                  (-> mano first :looped-words-of-buddha/index)))))))
