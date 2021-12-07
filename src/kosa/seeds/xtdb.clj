(ns kosa.seeds.xtdb
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [kuti.support :refer [path-join]]
            [kuti.storage.core :as storage-core]
            [xtdb.api :as xt]
            [joplin.xtdb.database :as d])
  (:import [java.net URI]))

(def leaf-attachment
  {:xt/id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb",
   :kuti/type :attm
   :attm/key "d54d85868f2963a4efee91e5c86e1679",
   :attm/service-name :disk,
   :attm/filename "bodhi-leaf.jpg",
   :attm/checksum "48fbe806b00c7696838eee7e5172403f",
   :attm/content-type "image/jpeg",
   :attm/identified true,
   :attm/metadata "",
   :attm/byte-size 109334})

(def raindrop-attachment
  {:xt/id #uuid "f7158192-42e7-4d96-be88-3144b9c1994e",
   :kuti/type :attm
   :attm/key "09d54922cf16064515a03c9168552462",
   :attm/service-name :disk,
   :attm/filename "bodhi-raindrops.jpg",
   :attm/checksum "ce2a70d30aaa2f9fc633d13d37f4c8ad",
   :attm/content-type "image/jpeg",
   :attm/identified true,
   :attm/metadata "",
   :attm/byte-size 452474})

(def buddha-attachment
  {:xt/id #uuid "b5d46c3f-da64-4881-9382-1ae3773d1a9c"
   :kuti/type :attm
   :attm/key "cfb6470bc83d7cffe8d171485015d70f",
   :attm/service-name :disk,
   :attm/filename "buddha.jpg",
   :attm/checksum "63f1ea8b25e47c2d467bf6b1b636c249",
   :attm/content-type "image/jpeg",
   :attm/identified true,
   :attm/metadata "",
   :attm/byte-size 27100})

(defn copy-attachments! []
  (doseq [a [leaf-attachment raindrop-attachment buddha-attachment]]
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
                          :stacked-inspiration/header "Inspiration"
                          :stacked-inspiration/shareable true
                          :stacked-inspiration/bookmarkable true
                          :stacked-inspiration/text "Mangal ho"
                          :stacked-inspiration/image-attachment-id #uuid "b5d46c3f-da64-4881-9382-1ae3773d1a9c"
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
                          :pali-word/published-at #time/instant "2021-03-21T01:47:36.547349Z"}]]]

      (d/transact! node txs (format "Seed '%s' failed to apply." (ns-name *ns*)))
      (log/info "...done."))))
