(ns kosa.api.handler
  (:require [kuti.support.time :as time]
            [kuti.support.digest :refer [uuid]]
            [kuti.record :as record]
            [kuti.storage.open-uri :as open-uri]
            [kosa.library.artefacts.image.db :as image-db]
            [kosa.mobile.today.pali-word.db :as pali-word-db]
            [kosa.mobile.today.words-of-buddha.db :as words-of-buddha-db]
            [kosa.mobile.today.doha.db :as doha-db]
            [kosa.mobile.today.stacked-inspiration.db :as stacked-inspiration-db]
            [ring.util.response :as resp]
            [clojure.tools.logging :as log]
            [kuti.mailer :as mailer]))

(defn status [_request]
  (let [test-url "http://download.pariyatti.org/dohas/001_Doha.mp3"
        test-res (try
                   (open-uri/download-uri! test-url)
                   (catch clojure.lang.ExceptionInfo e
                     e))
        node-status (record/status)
        mailer-status (try
                        (mailer/send-mail {:to "devnull@pariyatti.org"
                                           :subject "Kosa Status Check"
                                           :body "Kosa Status Check. Please Ignore this."})
                        (catch Throwable e
                          e))]
    (resp/response {:timestamp (time/now)
                    :mailer {:mailer-status mailer-status
                             :mailer-ok (not (instance? Throwable mailer-status))}
                    :xtdb-node-status (assoc node-status
                                             :node-ok (int? (:xtdb.kv/size node-status)))
                    :pariyatti-dot-org {:test-url test-url
                                        :test-file test-res
                                        :test-ok (not (instance? Throwable test-res))}})))

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
     :translations (map (fn [t] {:id (uuid) ;; mobile app demands an id
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
     :header "Words of the Buddha"
     :bookmarkable true
     :shareable true
     :words (:words-of-buddha/words card)
     :translations (map (fn [t] {:id (uuid)
                                 :language (first t)
                                 :translation (second t)})
                        (:words-of-buddha/translations card))
     :audio {:url (-> card :words-of-buddha/audio-attachment :attm/url)}
     :original_audio_url    (:words-of-buddha/original-audio-url card)
     :citepali     (:words-of-buddha/citepali card)
     :citepali_url (:words-of-buddha/citepali-url card)
     :citebook     (:words-of-buddha/citebook card)
     :citebook_url (:words-of-buddha/citebook-url card)
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
     :original_doha (:doha/original-doha card)
     :original_url (:doha/original-url card)
     :original_audio_url (:doha/original-audio-url card)
     :doha (:doha/doha card)
     :translations (map (fn [t] {:id (uuid) ;; mobile app demands an id
                                 :language (first t)
                                 :translation (second t)})
                        (:doha/translations card))}))

(defn stacked-inspiration->json [card]
  (let [published (:stacked-inspiration/published-at card)
        date (time/string (or published kosa-epoch))]
    {:type "stacked_inspiration"
     :id (:xt/id card)
     :published_at date
     :created_at date
     :updated_at date
     :header (or (-> card :stacked-inspiration/header) "Inspiration of the Day")
     :bookmarkable true
     :shareable true
     :text (:stacked-inspiration/text card)
     :image {:url (-> card :stacked-inspiration/image-attachment :attm/url)}}))

(defn today-list []
  (vec (concat
        (map pali-word->json (pali-word-db/list))
        (map words-of-buddha->json (words-of-buddha-db/list))
        (map doha->json (doha-db/list))
        (map stacked-inspiration->json (stacked-inspiration-db/list)))))

(defn paginate [cards limit offset]
  (vec (take limit (drop offset cards))))

(defn today-paginated [req]
  (let [{{:keys [limit offset]} :params} req]
    (if (and limit offset)
      (paginate (today-list) (parse-long limit) (parse-long offset))
      (today-list))))

(defn today [req]
  (resp/response (today-paginated req)))
