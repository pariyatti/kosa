(ns kosa.search.handler
  (:require [kosa.library.artefacts.image.db :as image-db]
            [ring.util.response :as resp]))

;; TODO: replace with actual full-text search
;;       try: http://localhost:3000/api/v1/search?q=crux-logo.png
(defn search* [list text]
  (if (nil? text)
    list
    (filter (fn [s] (clojure.string/includes?
                     (clojure.string/lower-case s)
                     (clojure.string/lower-case text)))
            list)))

(defn search [req]
  (let [text (-> req :params :q)
        list (image-db/search text)]
    (resp/response
     list)))
