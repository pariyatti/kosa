(ns kosa.seeds.crux
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [kutis.support :refer [path-join]]
            [kutis.storage :as storage]
            [crux.api :as x]
            [joplin.crux.database :as d]))

(def leaf-attachment
  {:crux.db/id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb",
   :type "attachment"
   :key "d54d85868f2963a4efee91e5c86e1679",
   :service-name :disk,
   :filename "bodhi-leaf.jpg",
   :checksum "48fbe806b00c7696838eee7e5172403f",
   :content-type "image/jpeg",
   :identified true,
   :metadata "",
   :byte-size 109334})

(def raindrop-attachment
  {:crux.db/id #uuid "f7158192-42e7-4d96-be88-3144b9c1994e",
   :type "attachment"
   :key "09d54922cf16064515a03c9168552462",
   :service-name :disk,
   :filename "bodhi-raindrops.jpg",
   :checksum "ce2a70d30aaa2f9fc633d13d37f4c8ad",
   :content-type "image/jpeg",
   :identified true,
   :metadata "",
   :byte-size 452474})

(def buddha-attachment
  {:crux.db/id #uuid "b5d46c3f-da64-4881-9382-1ae3773d1a9c",
   :type "attachment"
   :key "cfb6470bc83d7cffe8d171485015d70f",
   :service-name :disk,
   :filename "buddha.jpg",
   :checksum "63f1ea8b25e47c2d467bf6b1b636c249",
   :content-type "image/jpeg",
   :identified true,
   :metadata "",
   :byte-size 27100})

(defn copy-attachments! []
  (doseq [a [leaf-attachment raindrop-attachment buddha-attachment]]
    (io/copy (io/file (path-join "resources/joplin/seed-attachments"
                                 (:filename a)))
             (io/file (path-join "resources/storage/"
                                 (storage/attached-filename a))))))

(defn run [target & _args]
  (log/info "Seeding Crux...")
  (log/info "Adding attachments...")
  (copy-attachments!)
  (log/info "Adding entities...")
  (with-open [node (d/get-node (:db :conf target))]
    (let [txs [[:crux.tx/put {:crux.db/id #uuid "c58027f8-7c00-46d9-8338-6289e70ad299",
                              :type "image-artefact",
                              :modified-at #time/instant "2021-03-21T01:47:02.508768Z",
                              :searchables "bodhi leaf jpg bodhi-leaf.jpg",
                              :image-attachment-id #uuid "729755d4-e85f-43f7-9aa5-79c4ab6fbceb"}]

               [:crux.tx/put leaf-attachment]

               [:crux.tx/put {:crux.db/id #uuid "dbfd9e4a-f2a0-4b88-b312-035fdc25c736",
                              :type "image-artefact",
                              :modified-at #time/instant "2021-03-21T01:47:25.197224Z",
                              :searchables "bodhi raindrops jpg bodhi-raindrops.jpg",
                              :image-attachment-id #uuid "f7158192-42e7-4d96-be88-3144b9c1994e"}]

               [:crux.tx/put raindrop-attachment]

               [:crux.tx/put {:crux.db/id #uuid "54382efa-e597-4bbe-9197-65ebb3a0ebb3",
                              :type "image-artefact",
                              :modified-at #time/instant "2021-03-21T01:47:36.547349Z",
                              :searchables "buddha jpg buddha.jpg",
                              :image-attachment-id #uuid "b5d46c3f-da64-4881-9382-1ae3773d1a9c"}]

               [:crux.tx/put buddha-attachment]]]

      (d/transact! node txs (format "Seed '%s' failed to apply." (ns-name *ns*)))
      (log/info "...done."))))
