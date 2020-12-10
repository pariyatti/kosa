(ns kosa-crux.library.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa-crux.layouts.library :as l]))

(defn index []
  (l/app "Home"
         [:div {:class "section-all-cards"}

          [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Library Artefacts"]
            [:span {:class "page-subtitle"} "Artefacts are concrete library resources like books and audio files."]]]

          [:ul {:class "card-types"}
           [:li {:class "card-type"}
            [:h3 "Documents"]
            [:p "Document examples include: book, essay, article, transcript, excerpt, quote"]
            ;; "&lt;%= link_to &#39;Manage Cards&#39;, cards_overlay_inspiration_cards_path,class: &#39;link&#39;%&gt;\n            &lt;%= link_to &#39;Create Card&#39;, new_cards_overlay_inspiration_card_path, class: &#39;btn btn-primary&#39;%&gt;"
            ]
           [:li {:class "card-type"}
            [:h3 "Images"]
            [:p "Image examples include: photos, artwork, scanned documents"]
            [:a.link {:href "/library/artefacts/images"}
             "Manage Images"]
            [:a.btn.btn-primary {:href "/library/artefacts/image/new"}
             "Create Image"]]
           [:li {:class "card-type"}
            [:h3 "Audio"]
            [:p "Audio examples include: interviews, Q&A, lectures, discourses, chanting"]
            ;; [:a.link {:href "/publisher/today/pali_word_cards"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "/publisher/today/pali_word_card/new"}
            ;;  "Create Card"]
            ]
           [:li {:class "card-type"}
            [:h3 "Video"]
            [:p "Video examples include: lectures, dhamma talks, documentaries"]
            ;; [:a.link {:href "/publisher/today/pali_word_cards"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "/publisher/today/pali_word_card/new"}
            ;;  "Create Card"]
            ]]

          [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Metadata"]
            [:span {:class "page-subtitle"} "Metadata are not resources but rather descriptions of resources."]]]

          [:ul {:class "card-types"}
           [:li {:class "card-type"}
            [:h3 "Authors"]
            [:p ""]
            ;; "&lt;%= link_to &#39;Manage Cards&#39;, cards_overlay_inspiration_cards_path,class: &#39;link&#39;%&gt;\n            &lt;%= link_to &#39;Create Card&#39;, new_cards_overlay_inspiration_card_path, class: &#39;btn btn-primary&#39;%&gt;"
            ]
           [:li {:class "card-type"}
            [:h3 "Locations"]
            [:p ""]
            [:a.link {:href "/library/metadata/locations"}
             "Manage Locations"]
            [:a.btn.btn-primary {:href "/library/metadata/location/new"}
             "Create Location"]]
           [:li {:class "card-type"}
            [:h3 "Audiences"]
            [:p ""]
            ;; [:a.link {:href "/publisher/today/pali_word_cards"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "/publisher/today/pali_word_card/new"}
            ;;  "Create Card"]
            ]
           [:li {:class "card-type"}
            [:h3 "Topics"]
            [:p ""]
            ;; [:a.link {:href "/publisher/today/pali_word_cards"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "/publisher/today/pali_word_card/new"}
            ;;  "Create Card"]
            ]

           ;; TODO: add Collections and Tags
           ;; TODO: extract all this repeating view code
           ]]))
