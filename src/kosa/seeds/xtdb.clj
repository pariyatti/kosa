(ns kosa.seeds.xtdb
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [kuti.support :refer [path-join]]
            [kuti.storage.core :as storage-core]
            [xtdb.api :as xt]
            [joplin.xtdb.database :as d])
  (:import [java.net URI]))

(def leaf-attachment-id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb")
(def leaf-attachment
  {:xt/id leaf-attachment-id
   :kuti/type :attm
   :attm/key "d54d85868f2963a4efee91e5c86e1679",
   :attm/service-name :disk,
   :attm/filename "bodhi-leaf.jpg",
   :attm/checksum "48fbe806b00c7696838eee7e5172403f",
   :attm/content-type "image/jpeg",
   :attm/identified true,
   :attm/metadata "",
   :attm/byte-size 109334})

(def raindrop-attachment-id #uuid "f7158192-42e7-4d96-be88-3144b9c1994e")
(def raindrop-attachment
  {:xt/id raindrop-attachment-id
   :kuti/type :attm
   :attm/key "09d54922cf16064515a03c9168552462",
   :attm/service-name :disk,
   :attm/filename "bodhi-raindrops.jpg",
   :attm/checksum "ce2a70d30aaa2f9fc633d13d37f4c8ad",
   :attm/content-type "image/jpeg",
   :attm/identified true,
   :attm/metadata "",
   :attm/byte-size 452474})

(def buddha-attachment-id #uuid "b5d46c3f-da64-4881-9382-1ae3773d1a9c")
(def buddha-attachment
  {:xt/id buddha-attachment-id
   :kuti/type :attm
   :attm/key "cfb6470bc83d7cffe8d171485015d70f",
   :attm/service-name :disk,
   :attm/filename "buddha.jpg",
   :attm/checksum "63f1ea8b25e47c2d467bf6b1b636c249",
   :attm/content-type "image/jpeg",
   :attm/identified true,
   :attm/metadata "",
   :attm/byte-size 27100})

(def buddha-mp3-attachment-id #uuid "f63417b2-1404-4eb5-81dd-017b8f86db64")
(def buddha-mp3-attachment
  {:xt/id buddha-mp3-attachment-id
   :kuti/type :attm,
   :attm/byte-size 323712,
   :attm/content-type "audio/mpeg",
   :attm/filename "dhammapada_23_333.mp3"
   :attm/metadata "",
   :attm/updated-at #time/instant "2021-12-07T07:18:28.150485Z",
   :attm/checksum "b5d7f875bd2324e40ce991add4d5e4f5",
   :attm/service-name :disk,
   :attm/identified true,
   :attm/key "968ed1ab715dbb71262b27161f6202f8"})

(def doha-mp3-attachment-id #uuid "29d91967-1b06-4e3d-9e0a-9f20fa43775e")
(def doha-mp3-attachment
  {:xt/id doha-mp3-attachment-id
   :kuti/type :attm,
   :attm/byte-size 359127,
   :attm/content-type "audio/mpeg",
   :attm/filename "066_Doha.mp3"
   :attm/metadata "",
   :attm/updated-at #time/instant "2022-02-21T03:02:07.301087Z",
   :attm/checksum "5cc1fe56d1479b71431144d86895739c",
   :attm/service-name :disk,
   :attm/identified true,
   :attm/key "4010985eb9f5b5e26a4decf14139bd1e"})


(defn copy-attachments! []
  (doseq [a [leaf-attachment
             raindrop-attachment
             buddha-attachment
             buddha-mp3-attachment
             doha-mp3-attachment]]
    (io/copy (io/file (path-join "resources/joplin/seed-attachments"
                                 (:attm/filename a)))
             (io/file (path-join "resources/storage/"
                                 (storage-core/attached-filename a))))))

(defn run [target & _args]
  (log/info "Seeding XTDB...")
  (log/info "Adding attachments...")
  (copy-attachments!)
  (log/info "Adding entities...")
  (with-open [node (d/get-node (-> target :db :conf))]
    (let [txs [[::xt/put {:xt/id #uuid "c58027f8-7c00-46d9-8338-6289e70ad299",
                          :kuti/type :image-artefact,
                          :image-artefact/updated-at #time/instant "2021-03-21T01:47:02.508768Z",
                          :image-artefact/searchables "bodhi leaf jpg bodhi-leaf.jpg",
                          :image-artefact/image-attachment-id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb"}]

               [::xt/put leaf-attachment]

               [::xt/put {:xt/id #uuid "dbfd9e4a-f2a0-4b88-b312-035fdc25c736",
                          :kuti/type :image-artefact,
                          :image-artefact/updated-at #time/instant "2021-03-21T01:47:25.197224Z",
                          :image-artefact/searchables "bodhi raindrops jpg bodhi-raindrops.jpg",
                          :image-artefact/image-attachment-id #uuid "f7158192-42e7-4d96-be88-3144b9c1994e"}]

               [::xt/put raindrop-attachment]

               [::xt/put {:xt/id #uuid "54382efa-e597-4bbe-9197-65ebb3a0ebb3",
                          :kuti/type :image-artefact,
                          :image-artefact/updated-at #time/instant "2021-03-21T01:47:36.547349Z",
                          :image-artefact/searchables "buddha jpg buddha.jpg",
                          :image-artefact/image-attachment-id #uuid "b5d46c3f-da64-4881-9382-1ae3773d1a9c"}]

               [::xt/put buddha-attachment]

               [::xt/put {:xt/id #uuid "52f55f79-598f-4b68-805e-0d511a9e3d87"
                          :kuti/type :stacked-inspiration
                          :stacked-inspiration/header "Inspiration of the Day"
                          :stacked-inspiration/shareable true
                          :stacked-inspiration/bookmarkable true
                          :stacked-inspiration/text "\"We are shaped by our thoughts; we become what we think. When the mind is pure, joy follows like a shadow that never leaves.\""
                          :stacked-inspiration/image-attachment-id leaf-attachment-id
                          :stacked-inspiration/updated-at #time/instant "2021-03-21T01:47:36.547349Z"
                          :stacked-inspiration/published-at #time/instant "2021-03-21T01:47:36.547349Z"}]

               [::xt/put {:xt/id #uuid "1fbe175d-f0bd-47d6-83b9-3ecce030c6c0"
                          :kuti/type :pali-word
                          :pali-word/shareable true
                          :pali-word/bookmarkable true
                          :pali-word/header "Pāli Word of the Day"
                          :pali-word/original-pali "kosa"
                          :pali-word/original-url (URI. "https://www.digitalpalireader.online/_dprhtml/index.html?analysis=kosa")
                          :pali-word/pali "kosa"
                          :pali-word/translations [["eng" "store-room; treasury"]]
                          :pali-word/updated-at #time/instant "2021-03-21T01:47:36.547349Z"
                          :pali-word/published-at #time/instant "2021-03-21T01:47:36.547349Z"}]

               ;; TODO: put an `audio-attachment` to find in the UI

               [::xt/put buddha-mp3-attachment]

               [::xt/put {:xt/id #uuid "99be175d-f0bd-47d6-83b9-3ecce030c699"
                          :kuti/type :words-of-buddha
                          :words-of-buddha/shareable true
                          :words-of-buddha/bookmarkable true
                          :words-of-buddha/original-words "Susukhaṃ vata jīvāma,\nverinesu averino.\nVerinesu manussesu,\nviharāma averino."
                          :words-of-buddha/original-url (URI. "")
                          :words-of-buddha/words "Susukhaṃ vata jīvāma,\nverinesu averino.\nVerinesu manussesu,\nviharāma averino."
                          :words-of-buddha/audio-attachment-id buddha-mp3-attachment-id
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
                          :words-of-buddha/updated-at #time/instant "2021-03-21T01:47:36.547349Z"
                          :words-of-buddha/published-at #time/instant "2021-03-21T01:47:36.547349Z"}]

               [::xt/put doha-mp3-attachment]

               [::xt/put {:xt/id #uuid "1a006024-5f92-4799-b18b-7e19173aa291"
                          :kuti/type :doha
                          :doha/original-doha "Duralabha jīvana manuja kā, \nduralabha Dharama milāpa. \nDhanya bhāga! donoṅ mile, \ndūra kareṅ bhava tāpa."
                          :doha/original-url (URI. "")
                          :doha/doha "Duralabha jīvana manuja kā, \nduralabha Dharama milāpa. \nDhanya bhāga! donoṅ mile, \ndūra kareṅ bhava tāpa."
                          :doha/audio-attachment-id doha-mp3-attachment-id
                          :doha/original-audio-url (URI. "http://download.pariyatti.org/dohas/066_Doha.mp3")
                          :doha/translations [
                                              ["eng" "Rare is human life, \nrare to encounter the Dhamma. \nWe are fortunate to have both; \nlet us banish the torment of becoming. \n\n–S.N. Goenka"]
                                              ["lit" "Žmogaus gyvenimas ¬– ypatingas,  \nPatirti Dhammą pasiseka ne kiekvienam. \nTurime abu, todėl esame laimingi, \nSunaikinkime tapsmo kančias. \n\n–S.N. Goenka"]
                                              ["por" "Rara é a vida humana,\nraro é encontrar o Dhamma.\nSomos afortunados por ter ambos;\nvamos banir o tormento do vir a ser.\n\n–S.N. Goenka"]
                                              ["zho-hant" "人身稀有，\n正法難遇。\n我們幸得兩者；\n願斷除輪迴的折磨。\n\n─葛印卡老師"]
                                              ]
                          :doha/updated-at #time/instant "2022-02-21T03:11:20.410897Z"
                          :doha/published-at #time/instant "2022-02-21T03:11:20.410897Z"}]
               ]]

      (d/transact! node txs (format "Seed '%s' failed to apply." (ns-name *ns*)))
      (log/info "...done."))))
