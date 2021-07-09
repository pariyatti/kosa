(ns kosa.library.views
  (:require [kosa.layouts.library :as l]
            [kosa.views :as v]))

(defn index [req]
  (l/app req "Home"
         [:div {:class "section-all-artefacts"}

          [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Library Artefacts"]
            [:span {:class "page-subtitle"} "Artefacts are concrete library resources like books and audio files."]]]

          [:ul {:class "artefacts-wrapper"}
           [:li {:class "artefact"}
            [:h3 "Documents"]
            [:p "Document examples include: book, essay, article, transcript, excerpt, quote"]
            ;; [:a.link {:href "#todo"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "#todo"}
            ;;  "Create Card"]
            ]
           [:li {:class "artefact"}
            [:h3 "Images"]
            [:p "Image examples include: photos, artwork, scanned documents"]
            [:a.link {:href (v/index-path req :images)}
             "Manage Images"]
            [:a.btn.btn-primary {:href (v/new-path req :images)}
             "Create Image"]]
           [:li {:class "artefact"}
            [:h3 "Audio"]
            [:p "Audio examples include: interviews, Q&A, lectures, discourses, chanting"]
            ;; [:a.link {:href "#todo"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "#todo"}
            ;;  "Create Card"]
            ]
           [:li {:class "artefact"}
            [:h3 "Video"]
            [:p "Video examples include: lectures, dhamma talks, documentaries"]
            ;; [:a.link {:href "#todo"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "#todo"}
            ;;  "Create Card"]
            ]]]

         [:div "&nbsp;"]

         [:div {:class "section-all-metadata"}
          [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Metadata"]
            [:span {:class "page-subtitle"} "Metadata are not resources but rather descriptions of resources."]]]

          [:ul {:class "metadata-wrapper"}
           [:li {:class "metadatum"}
            [:h3 "Authors"]
            [:p ""]
            ;; [:a.link {:href "#todo"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "#todo"}
            ;;  "Create Card"]
            ]
           [:li {:class "metadatum"}
            [:h3 "Locations"]
            [:p ""]
            [:a.link {:href "/library/metadata/locations"}
             "Manage Locations"]
            [:a.btn.btn-primary {:href "/library/metadata/location/new"}
             "Create Location"]]
           [:li {:class "metadatum"}
            [:h3 "Audiences"]
            [:p ""]
            ;; [:a.link {:href "#todo"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "#todo"}
            ;;  "Create Card"]
            ]
           [:li {:class "metadatum"}
            [:h3 "Topics"]
            [:p ""]
            ;; [:a.link {:href "#todo"}
            ;;  "Manage Cards"]
            ;; [:a.btn.btn-primary {:href "#todo"}
            ;;  "Create Card"]
            ]

           ;; TODO: add Collections and Tags
           ;; TODO: extract all this repeating view code
           ]]))
