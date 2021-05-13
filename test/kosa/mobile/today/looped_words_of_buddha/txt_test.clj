(ns kosa.mobile.today.looped-words-of-buddha.txt-test
  (:require [kosa.mobile.today.looped-words-of-buddha.txt :as sut]
            [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures])
  (:import [java.net URI]))

(use-fixtures :once
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(deftest parsing-txt-file
  (testing "parses out one day without whitespace or separators"
    (let [f (file-fixtures/file "words_of_buddha_raw.txt")
          txt (slurp f)]
      (is (= {:looped-words-of-buddha/words "Gahakāraka, diṭṭhosi!\nPuna gehaṃ na kāhasi.\nSabbā te phāsukā bhaggā gahakūṭaṃ visaṅkhataṃ.\nVisaṅkhāragataṃ cittaṃ;\ntaṇhānaṃ khayamajjhagā."
              :looped-words-of-buddha/audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_11_154.mp3")
              :looped-words-of-buddha/translations [["en" "O house-builder, you are seen!\nYou will not build this house again.\nFor your rafters are broken and your ridgepole shattered.\nMy mind has reached the Unconditioned;\nI have attained the destruction of craving."]]
              :looped-words-of-buddha/citation "Dhammapada 11.154"
              :looped-words-of-buddha/citation-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul10.xml#para154")
              ;; TODO: we need to decide if we are tracking store info for
              ;;       all translations or not. -sd
              ;; :looped-words-of-buddha/store-title "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
              ;; :looped-words-of-buddha/store-url "https://store.pariyatti.org/Dhammapada-The-BP203ME-Pocket-Version_p_2513.html"
              }
             (first (sut/parse txt "en")))))))
