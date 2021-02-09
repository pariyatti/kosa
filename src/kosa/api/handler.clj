(ns kosa.api.handler
  (:require [kutis.support.time :as time]
            [kosa.library.artefacts.image.db :as image-db]
            [kosa.mobile.today.pali-word.db :as pali-word-db]
            [ring.util.response :as resp]))

(defn search [req]
  (let [text (-> req :params :q)
        list (image-db/search-for text)]
    (resp/response
     list)))

(defn pali-word->json [word]
  ;; TODO: how much of the Crux entity do we just want to hand over verbatim?
  (let [date (time/fmt (:published-at word))]
    {:type "pali_word"
     :id (:crux.db/id word)
     :published_at date
     :created_at date
     :updated_at date
     :header "Pāli Word of the Day"
     :bookmarkable true
     :shareable true
     :pali (:pali word)
     :translations (map (fn [t] {:id (str (java.util.UUID/randomUUID))
                                 :language (first t)
                                 :translation (second t)})
                        (:translations word))
     :audio {:url (format "/uploads/cards/pali_word_card/audio/%s" (:crux.db/id word))}}))

(defn today-list []
  (map pali-word->json (pali-word-db/list)))

(defn today [req]
  (resp/response (today-list)))
