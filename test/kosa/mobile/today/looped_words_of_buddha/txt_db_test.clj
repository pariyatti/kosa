(ns ^:database kosa.mobile.today.looped-words-of-buddha.txt-db-test
  (:require [kosa.mobile.today.looped-words-of-buddha.txt :as sut]
            [kosa.mobile.today.looped-words-of-buddha.db :as db]
            [kosa.mobile.today.looped.txt :as looped]
            [clojure.test :refer :all]
            [kosa.fixtures.file-fixtures :as file-fixtures]
            [kosa.fixtures.model-fixtures :as model]
            [kuti.fixtures.record-fixtures :as record-fixtures]
            [kuti.fixtures.time-fixtures :as time-fixtures]
            [kuti.fixtures.storage-fixtures :as storage-fixtures]
            [kuti.support.time :as time]
            [kuti.support.debugging :refer :all])
  (:import [java.net URI]))

(use-fixtures :once
  storage-fixtures/set-service-config)

(use-fixtures :each
  time-fixtures/freeze-clock-1995
  record-fixtures/force-destroy-db
  record-fixtures/force-migrate-db
  record-fixtures/force-start-db)

(def i (sut/->BuddhaIngester))

(deftest ingesting-txt-file
  (testing "inserts entries into db"
    (let [f (file-fixtures/file "words_of_buddha_raw.txt")]
      (sut/ingest f "eng")
      (is (= 3 (count (db/list)))))))

(deftest merging-entities
  (testing "ignore identical entities"
    (db/save! (model/looped-words-of-buddha
               {:looped-words-of-buddha/words "Manopubbaṅgamā dhammā,"
                :looped-words-of-buddha/translations [["eng" "Mind precedes all phenomena,"]]
                :looped-words-of-buddha/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-words-of-buddha
                          {:looped-words-of-buddha/words "Manopubbaṅgamā dhammā,"
                           :looped-words-of-buddha/translations [["eng" "Mind precedes all phenomena,"]]
                           :looped-words-of-buddha/published-at (time/parse "2012-01-01")}))
    (let [mano (db/find-all :looped-words-of-buddha/words "Manopubbaṅgamā dhammā,")]
      (is (= 1 (count mano)))
      (is (= (time/parse "2008-01-01")
             (-> mano first :looped-words-of-buddha/published-at)))))

  (testing "merge additional languages if merged is not identical"
    (db/save! (model/looped-words-of-buddha
               {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                :looped-words-of-buddha/translations [["eng" "Speak not harshly to anyone,"]
                                                      ["hin" "किसी से कटुता से न बोलें,"]]
                :looped-words-of-buddha/published-at (time/parse "2008-01-01")}))
    (looped/db-insert! i (model/looped-words-of-buddha
                       {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                        :looped-words-of-buddha/translations [["fra" "Ne parlez pas durement à qui que ce soit,"]
                                                              ["spa" "No hables agresivamente a nadie;"]]
                        :looped-words-of-buddha/published-at (time/parse "2012-01-01")}))
    (let [voca (db/find-all :looped-words-of-buddha/words "Māvoca pharusaṃ kañci,")]
      (is (= 1 (count  voca)))
      (is (= [["eng" "Speak not harshly to anyone,"]
              ["hin" "किसी से कटुता से न बोलें,"]
              ["fra" "Ne parlez pas durement à qui que ce soit,"]
              ["spa" "No hables agresivamente a nadie;"]]
             (-> voca first :looped-words-of-buddha/translations))))))

(deftest citations
  (testing "other translations do not erase english citations"
    (db/save! (model/looped-words-of-buddha
               #:looped-words-of-buddha
               {:words "Māvoca pharusaṃ kañci,"
                :translations [["eng" "Speak not harshly to anyone,"]]
                :citepali "Dhammapada 10.133"
                :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul9.xml#para133")
                :citebook "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
                :citebook-url (URI. "https://store.pariyatti.org/Dhammapada-The-BP203ME-Pocket-Version_p_2513.html")
                :published-at (time/parse "2008-01-01")}))

    (looped/db-insert! i (model/looped-words-of-buddha
                       #:looped-words-of-buddha
                       {:words "Māvoca pharusaṃ kañci,"
                        :translations [["spa" "No hables agresivamente a nadie;"]]
                        :citepali "Dhammapada 10.133"
                        :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul9.xml#para133")
                        :citebook "Dhammapada, traducción de Bhikkhu Nandisena, México, Dhammodaya Ediciones"
                        :citebook-url (URI. "http://dhammodaya.btmar.org/content/dhammapada%E2%80%94precio-y-compra-en-l%C3%ADnea")
                        :published-at (time/parse "2012-01-01")}))

    (let [voca (db/find-all :looped-words-of-buddha/words "Māvoca pharusaṃ kañci,")]
      (is (= "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
             (-> voca first :looped-words-of-buddha/citebook)))))

  (testing "english citations overwrite other languages, since other citations will come from i18n"
    (db/save! (model/looped-words-of-buddha
               #:looped-words-of-buddha
               {:words "Manopubbaṅgamā dhammā,"
                :translations [["spa" "La mente precede todo fenómeno,"]]
                :citepali "Dhammapada 1.1, 1.2"
                :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul0.xml#para1")
                :citebook "Resumen De Las Charlas del Curso de Diez Dias"
                :citebook-url (URI. "http://store.pariyatti.org/Discourse-Summaries--Spanish_p_2654.html")
                :published-at (time/parse "2012-01-01")}))

    (looped/db-insert! i (model/looped-words-of-buddha
                       #:looped-words-of-buddha
                       {:words "Manopubbaṅgamā dhammā,"
                        :translations [["eng" "Mind precedes all phenomena,"]]
                        :citepali "Dhammapada 1.1, 1.2"
                        :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul0.xml#para1")
                        :citebook "The Discourse Summaries by S.N. Goenka"
                        :citebook-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")
                        :published-at (time/parse "2008-01-01")}))

    (let [voca (db/find-all :looped-words-of-buddha/words "Manopubbaṅgamā dhammā,")]
      (is (= "The Discourse Summaries by S.N. Goenka"
             (-> voca first :looped-words-of-buddha/citebook))))))

(deftest indexing
  (testing "index auto-increments"
    (looped/db-insert! i (model/looped-words-of-buddha
                       {:looped-words-of-buddha/words "Manopubbaṅgamā dhammā,"
                        :looped-words-of-buddha/translations [["eng" "Mind precedes all phenomena,"]]}))
    (looped/db-insert! i (model/looped-words-of-buddha
                       {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                        :looped-words-of-buddha/translations [["eng" "Speak not harshly to anyone,"]]}))
    (let [mano (db/find-all :looped-words-of-buddha/words "Manopubbaṅgamā dhammā,")
          voca (db/find-all :looped-words-of-buddha/words "Māvoca pharusaṃ kañci,")]
      (is (= 1 (- (-> voca first :looped-words-of-buddha/index)
                  (-> mano first :looped-words-of-buddha/index)))))))

(deftest indexing-edge-cases
  (testing "index does not increment if index already exists for card"
    (looped/db-insert! i (model/looped-words-of-buddha
                       {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                        :looped-words-of-buddha/translations [["eng" "Speak not harshly to anyone,"]]}))
    (looped/db-insert! i (model/looped-words-of-buddha
                       {:looped-words-of-buddha/words "Māvoca pharusaṃ kañci,"
                        :looped-words-of-buddha/translations [["fra" "Ne parlez pas durement à qui que ce soit,"]]}))
    (let [voca (db/find-all :looped-words-of-buddha/words "Māvoca pharusaṃ kañci,")]
      (is (= 0 (-> voca first :looped-words-of-buddha/index))))))

(deftest mp3s
  (testing "downloads and attaches mp3"
    (let [card (model/looped-words-of-buddha
                {:looped-words-of-buddha/audio-attachment
                 nil
                 :looped-words-of-buddha/original-audio-url
                 (URI. "http://download.pariyatti.org/dwob/sutta_nipata_3_710.mp3")})
          e (looped/download-attachments! i "eng" card)]
      (is (= "sutta_nipata_3_710.mp3"
             (-> e :looped-words-of-buddha/audio-attachment :attm/filename)))
      (is (= 184645
             (-> e :looped-words-of-buddha/audio-attachment :attm/byte-size))))))
