(ns kosa.api.handler
  (:require [kuti.support.time :as time]
            [kuti.support.digest :refer [uuid]]
            [kosa.library.artefacts.image.db :as image-db]
            [kosa.mobile.today.pali-word.db :as pali-word-db]
            [kosa.mobile.today.words-of-buddha.db :as words-of-buddha-db]
            [kosa.mobile.today.stacked-inspiration.db :as stacked-inspiration-db]
            [ring.util.response :as resp]))

(defn search [req]
  (let [text (-> req :params :q)
        list (image-db/search-for text)]
    (resp/response
     list)))

(def kosa-epoch "2020-12-12T00:00:00.000Z")

(defn pali-word->json [word]
  ;; TODO: how much of the Crux entity do we just want to hand over verbatim?
  (let [published (:pali-word/published-at word)
        date (time/string (or published kosa-epoch))]
    {:type "pali_word"
     :id (:crux.db/id word)
     :published_at date
     :created_at date
     :updated_at date
     :header "Pāli Word of the Day"
     :bookmarkable true
     :shareable true
     :pali (:pali-word/pali word)
     :translations (map (fn [t] {:id (uuid)
                                 :language (first t)
                                 :translation (second t)})
                        (:pali-word/translations word))
     ;; NOTE: for the time-being, pali word cards do not have an audio
     ;;       file encoded. we can add this feature later, but we should
     ;;       be aware that :looped-pali-word never has an audio file. -sd
     :audio {:url ""}}))

(defn words-of-buddha->json [card]
  (let [published (:words-of-buddha/published-at card)
        date (time/string (or published kosa-epoch))]
    ;; TODO: this requires its own type, but the mobile app doesn't support
    ;;       that yet. -sd
    {:type "stacked_inspiration"
     :id (:crux.db/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header "Words of Buddha"
     :bookmarkable true
     :shareable true
     :text (:words-of-buddha/words card)
     ;; TODO: seed data?
     :image {:url "https://store.pariyatti.org/assets/images/Buddha_statue.jpg"}}))

(defn stacked-inspiration->json [card]
  (let [published (:stacked-inspiration/published-at card)
        date (time/string (or published kosa-epoch))]
    {:type "stacked_inspiration"
     :id (:crux.db/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header "Inspiration"
     :bookmarkable true
     :shareable true
     :text (:stacked-inspiration/text card)
     :image {:url (-> card :stacked-inspiration/image-attachment :attm/url)}}))

(defn today-list []
  (vec (concat
        (map pali-word->json (pali-word-db/list))
        (map words-of-buddha->json (words-of-buddha-db/list))
        (map stacked-inspiration->json (stacked-inspiration-db/list)))))

(defn today [req]
  (resp/response (today-list)))
