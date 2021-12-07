(ns kosa.api.handler
  (:require [kuti.support.time :as time]
            [kuti.support.digest :refer [uuid]]
            [kosa.library.artefacts.image.db :as image-db]
            [kosa.mobile.today.pali-word.db :as pali-word-db]
            [kosa.mobile.today.words-of-buddha.db :as words-of-buddha-db]
            [kosa.mobile.today.doha.db :as doha-db]
            [kosa.mobile.today.stacked-inspiration.db :as stacked-inspiration-db]
            [ring.util.response :as resp]))

(defn search [req]
  (let [text (-> req :params :q)
        list (image-db/search-for text)]
    (resp/response
     list)))

(def kosa-epoch "2020-12-12T00:00:00.000Z")

(defn pali-word->json [word]
  ;; TODO: how much of the XTDB entity do we just want to hand over verbatim?
  (let [published (:pali-word/published-at word)
        date (time/string (or published kosa-epoch))]
    {:type "pali_word"
     :id (:xt/id word)
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
    {:type "words_of_buddha"
     :id (:xt/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header "Words of Buddha"
     :bookmarkable true
     :shareable true
     :words (:words-of-buddha/words card)
     :translations (map (fn [t] {:language (first t)
                                 :translation (second t)})
                        (:words-of-buddha/translations card))
     :audio {:url (-> card :words-of-buddha/audio-attachment :attm/url)}
     :audio-url    (:words-of-buddha/audio-url card)
     :citepali     (:words-of-buddha/citepali card)
     :citepali-url (:words-of-buddha/citepali-url card)
     :citebook     (:words-of-buddha/citebook card)
     :citebook-url (:words-of-buddha/citebook-url card)
     ;; TODO: seed data?
     :image {:url "/uploads/kuti-d54d85868f2963a4efee91e5c86e1679-bodhi-leaf.jpg"}}))

(defn words-of-buddha->fake-json [card]
  (let [published (:words-of-buddha/published-at card)
        date (time/string (or published kosa-epoch))]
    ;; TODO: this requires its own type, but the mobile app doesn't support
    ;;       that yet. -sd
    {:type "stacked_inspiration"
     :id (:xt/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header "Words of Buddha"
     :bookmarkable true
     :shareable true
     :text (:words-of-buddha/words card)
     ;; TODO: seed data?
     :image {:url "/uploads/kuti-d54d85868f2963a4efee91e5c86e1679-bodhi-leaf.jpg"}}))

(defn doha->json [card]
  (let [published (:doha/published-at card)
        date (time/string (or published kosa-epoch))]
    {:type "doha"
     :id (:xt/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header "Daily Doha"
     :bookmarkable true
     :shareable true
     :doha (:doha/doha card)
     :translations (map (fn [t] {:language (first t)
                                 :translation (second t)})
                        (:doha/translations card))}))

(defn doha->fake-json [card]
  (let [published (:doha/published-at card)
        date (time/string (or published kosa-epoch))]
    ;; TODO: this requires its own type, but the mobile app doesn't support
    ;;       that yet. -sd
    {:type "stacked_inspiration"
     :id (:xt/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header "Daily Doha"
     :bookmarkable true
     :shareable true
     :text (:doha/doha card)
     :image {:url "/uploads/kuti-d54d85868f2963a4efee91e5c86e1679-bodhi-leaf.jpg"}}))

(defn stacked-inspiration->json [card]
  (let [published (:stacked-inspiration/published-at card)
        date (time/string (or published kosa-epoch))]
    {:type "stacked_inspiration"
     :id (:xt/id card)
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
        ;; (map words-of-buddha->fake-json (words-of-buddha-db/list))
        (map words-of-buddha->json (words-of-buddha-db/list))
        (map doha->fake-json (doha-db/list))
        ;; (map doha->json (doha-db/list))
        (map stacked-inspiration->json (stacked-inspiration-db/list)))))

(defn today [req]
  (resp/response (today-list)))
