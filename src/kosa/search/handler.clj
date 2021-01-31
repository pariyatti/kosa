(ns kosa.search.handler
  (:require [kosa.library.artefacts.image.db :as image-db]
            [ring.util.response :as resp]))

(defn search [req]
  (let [text (-> req :params :q)
        list (image-db/search-for text)]
    (resp/response
     list)))
