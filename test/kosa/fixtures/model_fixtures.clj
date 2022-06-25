(ns kosa.fixtures.model-fixtures
  (:require [kuti.support.digest :as digest])
  (:import [java.net URI]
           [java.util UUID]))

(defn pali-word [& [pw]]
  (merge {:xt/id #uuid "1fbe175d-f0bd-47d6-83b9-3ecce030c6c0"
          :kuti/type :pali-word
          :pali-word/original-pali "kosa"
          :pali-word/original-url (URI. "https://www.digitalpalireader.online/_dprhtml/index.html?analysis=kosa")
          :pali-word/pali "kosa"
          :pali-word/translations [["eng" "store-room; treasury"]]
          :pali-word/updated-at #time/instant "2001-04-22T00:00:00.000000Z"
          :pali-word/published-at #time/instant "2001-04-22T00:00:00.000000Z"}
         pw))

(defn looped-pali-word [pw]
  (merge {:looped-pali-word/pali "tiṇṇa"
          :looped-pali-word/original-pali "tiṇṇa"
          :looped-pali-word/original-url (URI. "")
          :looped-pali-word/translations
          [["eng" "gone through, overcome, one who has attained nibbāna"]]}
         pw))

(defn image-attachment [& [img]]
  (merge {:xt/id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb"
          :kuti/type :attm
          :attm/key "d54d85868f2963a4efee91e5c86e1679",
          :attm/service-name :disk,
          :attm/filename "bodhi-leaf.jpg",
          :attm/checksum "48fbe806b00c7696838eee7e5172403f",
          :attm/content-type "image/jpeg",
          :attm/identified true,
          :attm/metadata "",
          :attm/byte-size 109334}
         img))

(defn words-of-buddha-audio-attachment [& [aud]]
  (merge {:xt/id #uuid "f63417b2-1404-4eb5-81dd-017b8f86db64"
          :kuti/type :attm,
          :attm/byte-size 323712,
          :attm/content-type "audio/mpeg",
          :attm/filename "dhammapada_23_333.mp3"
          :attm/metadata "",
          :attm/updated-at #time/instant "2021-12-07T07:18:28.150485Z",
          :attm/checksum "b5d7f875bd2324e40ce991add4d5e4f5",
          :attm/service-name :disk,
          :attm/identified true,
          :attm/key "968ed1ab715dbb71262b27161f6202f8"}
         aud))

(defn words-of-buddha [& [wob]]
  (merge {:xt/id #uuid "99be175d-f0bd-47d6-83b9-3ecce030c699"
          :kuti/type :words-of-buddha
          :words-of-buddha/original-words "Susukhaṃ vata jīvāma,\nverinesu averino.\nVerinesu manussesu,\nviharāma averino."
          :words-of-buddha/original-url (URI. "")
          :words-of-buddha/words "Susukhaṃ vata jīvāma,\nverinesu averino.\nVerinesu manussesu,\nviharāma averino."
          :words-of-buddha/audio-attachment-id #uuid "f63417b2-1404-4eb5-81dd-017b8f86db64"
          :words-of-buddha/original-audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_15_197.mp3")
          :words-of-buddha/translations [
                                              ["eng" "Happy indeed we live,\nfriendly amidst the hostility.\nAmidst hostile people,\nwe dwell free from hatred."]
                                              ["fra" "Heureux en vérité nous vivons,\nAmicaux au milieu de l’downloadilité.\nParmi les gens downloadiles,\nNous demeurons, libres de haine."]
                                              ["ita" "Certamente viviamo felici,\namichevoli tra gente ostile.\nTra gente ostile \ndimoriamo liberi dall’odio."]
                                              ["por" "Felizes, de fato, vivemos, \namigáveis entre os downloadis. \nEntre as pessoas downloadis \npermanecemos livres do ódio."]
                                              ["spa" "En verdad vivimos bien felices, \nsin odios entre los que odian. \nEntre los hombres que odian \nsin odios vivimos."]
                                              ["srp" "Ah kako srećni živimo \nbez mržnje, okruženi mrziteljima; \nmeđu ljudima punim mržnje, \nbez mržnje boravimo."]
                                              ["zho-hant" "我們的確生活愉快， \n在敵意的環境裡保持友善。 \n處在敵對者之中， \n我們毫無恨意地安住。 \n《南傳法句經 15.197》"]
                                              ]
          :words-of-buddha/citepali "Dhammapada 15.197"
          :words-of-buddha/citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul14.xml#para197")
          :words-of-buddha/citebook "The Dhammapada: The Buddha's Path of Wisdom, translated from Pāli by Acharya Buddharakkhita"
          :words-of-buddha/citebook-url (URI. "https://store.pariyatti.org/The-Dhammapada-The-Buddhas-Path-of-Wisdom-Pocket-Edition_p_6305.html")
          :words-of-buddha/updated-at #time/instant "2011-01-23T00:00:00.000000Z"
          :words-of-buddha/published-at #time/instant "2011-01-23T00:00:00.000000Z"}
         wob))

(defn looped-words-of-buddha [wob]
  (merge #:looped-words-of-buddha
         {:original-words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :original-url (URI. "")
          :words "Tumhehi kiccamātappaṃ;\nAkkhātāro Tathāgatā.\nPaṭipannā pamokkhanti\njhāyino mārabandhanā."
          :audio-attachment (words-of-buddha-audio-attachment {})
          :original-audio-url (URI. "http://download.pariyatti.org/dwob/dhammapada_20_276.mp3")
          :translations [["eng" "You have to do your own work;\nEnlightened Ones will only show the way.\nThose who practise meditation\nwill free themselves from the chains of death."]]
          :citepali "Dhammapada 20.276"
          :citepali-url (URI. "http://tipitaka.org/romn/cscd/s0502m.mul19.xml#para276")
          :citebook "The Discourse Summaries by S.N. Goenka"
          :citebook-url (URI. "http://store.pariyatti.org/Discourse-Summaries_p_1650.html")}
         wob))

(defn doha-audio-attachment [& [aud]]
  (merge {:xt/id #uuid "29d91967-1b06-4e3d-9e0a-9f20fa43775e"
          :kuti/type :attm,
          :attm/byte-size 359127,
          :attm/content-type "audio/mpeg",
          :attm/filename "066_Doha.mp3"
          :attm/metadata "",
          :attm/updated-at #time/instant "2022-02-21T03:02:07.301087Z",
          :attm/checksum "5cc1fe56d1479b71431144d86895739c",
          :attm/service-name :disk,
          :attm/identified true,
          :attm/key "4010985eb9f5b5e26a4decf14139bd1e"}
         aud))

(defn doha [& [doha]]
  (merge {:xt/id #uuid "1a006024-5f92-4799-b18b-7e19173aa291"
          :kuti/type :doha
          :doha/original-doha "Duralabha jīvana manuja kā, \nduralabha Dharama milāpa. \nDhanya bhāga! donoṅ mile, \ndūra kareṅ bhava tāpa."
          :doha/original-url (URI. "")
          :doha/doha "Duralabha jīvana manuja kā, \nduralabha Dharama milāpa. \nDhanya bhāga! donoṅ mile, \ndūra kareṅ bhava tāpa."
          :doha/audio-attachment-id #uuid "29d91967-1b06-4e3d-9e0a-9f20fa43775e"
          :doha/original-audio-url (URI. "http://download.pariyatti.org/dohas/066_Doha.mp3")
          :doha/translations [
                                   ["eng" "Rare is human life, \nrare to encounter the Dhamma. \nWe are fortunate to have both; \nlet us banish the torment of becoming. \n\n–S.N. Goenka"]
                                   ["lit" "Žmogaus gyvenimas ¬– ypatingas,  \nPatirti Dhammą pasiseka ne kiekvienam. \nTurime abu, todėl esame laimingi, \nSunaikinkime tapsmo kančias. \n\n–S.N. Goenka"]
                                   ["por" "Rara é a vida humana,\nraro é encontrar o Dhamma.\nSomos afortunados por ter ambos;\nvamos banir o tormento do vir a ser.\n\n–S.N. Goenka"]
                                   ["zho-hant" "人身稀有，\n正法難遇。\n我們幸得兩者；\n願斷除輪迴的折磨。\n\n─葛印卡老師"]
                                   ]
          :doha/updated-at #time/instant "2012-01-01T00:00:00.000000Z"
          :doha/published-at #time/instant "2012-01-01T00:00:00.000000Z"}
         doha))

(defn looped-doha [doha]
  (merge #:looped-doha
         {:original-doha "Dasoṅ diśāṇa ke sabhī, \nprāṇī sukhiyā hoṅya. \nNirabhaya hoṅ, nirabaira hoṅ, \nsabhī nirāmaya hoṅya."
          :original-url (URI. "")
          :doha "Dasoṅ diśāṇa ke sabhī, \nprāṇī sukhiyā hoṅya. \nNirabhaya hoṅ, nirabaira hoṅ, \nsabhī nirāmaya hoṅya."
          :audio-attachment (words-of-buddha-audio-attachment {})
          :original-audio-url (URI. "http://download.pariyatti.org/dohas/117a_Doha.mp3")
          :translations [["eng" "In the ten directions, \nmay beings be happy, \nwithout fear or enmity; \nmay all be freed of ills. \n\n–S.N. Goenka"]]}
         doha))

(defn stacked-inspiration [& [si]]
  (merge {:xt/id #uuid "52f55f79-598f-4b68-805e-0d511a9e3d87"
          :kuti/type :stacked-inspiration
          :stacked-inspiration/text "\"We are shaped by our thoughts; we become what we think. When the mind is pure, joy follows like a shadow that never leaves.\""
          :stacked-inspiration/image-attachment-id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb"
          :stacked-inspiration/updated-at #time/instant "2011-03-21T00:00:00.000000Z"
          :stacked-inspiration/published-at #time/instant "2011-03-21T00:00:00.000000Z"}
         si))
