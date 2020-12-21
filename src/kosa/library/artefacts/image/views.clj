(ns kosa.library.artefacts.image.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa.layouts.library :as l]
            [kosa.views :as v]))

(defn header [req title breadcrumbs]
  [:div {:class "page-heading"}
   (interpose [:span "&nbsp; | &nbsp;"]
              (for [b breadcrumbs]
                [:span {:class "breadcrumb"}
                 [:a {:href (:path b)}
                  (format "&nbsp;Back to %s" (:text b))]]))
   [:div {:class "header-and-link flex"}
    [:h1 {:class "page-header"} title]]])

(defn index-header [req]
  [:div {:class "page-heading"}
   [:div {:class "breadcrumb"}
    [:a {:href (v/index-path req :library)}
     [:clr-icon {:shape "grid-view" :size "24"}]
     "&nbsp;Back to Library"]]
   [:div {:class "header-and-link flex"}
    [:h1 {:class "page-header"} "Image Artefacts"]
    [:a {:href (v/new-path req :images)}
     [:clr-icon {:shape "plus-circle" :size "24"}]
     "&nbsp;Create Image Artefact"]]])

(defn index [req images]
  (l/app "Image Artefacts"
         [:div {:class "section-all-artefacts"}
          (index-header req)
          [:div {:class "artefacts-wrapper"}
           (for [img images]
             [:div {:class "artefact"}
              [:div [:img {:src (:url img) :width "128" :height "128"}]]
              [:a {:href (v/show-path req :images img)}
               [:div (:url img)]]])]]))

(defn new-form* [req]
  [:form {:method "POST"
          :action (v/create-path req :images)
          :enctype "multipart/form-data"}
   [:div {:class "field"}
    (f/hidden-field :type "image_artefact")]
   [:div {:class "field"}
    (f/label :image-file "Image File:")
    (f/file-upload :image-file)]
   [:div {:class "actions"}
    (f/submit-button {:name "submit"} "Save")]])

(defn new [req]
  (l/app "New Image Artefact"
         (header req "New Image Artefact"
                 [{:path (v/index-path req :library)
                   :text "Library"}
                  {:path (v/index-path req :images)
                   :text "Images"}])
         [:div {:class "form-and-preview flex row"}
          (new-form* req)]))

(defn show* [image]
  (h/html
   [:table
    [:tr
     [:td "Image Preview:"]
     [:td [:img {:src (:url image) :width "128" :height "128"}]]]
    [:tr
     [:td "URL:"]
     [:td (:url image)]]
    [:tr
     [:td "Original URL:"]
     [:td (:original-url image)]]]))

(defn show [req image]
  (l/app "Show Image Artefact"
         (header req "Show Image Artefact"
                 [{:path (v/index-path req :library)
                   :text "Library"}
                  {:path (v/index-path req :images)
                   :text "Images"}])
         (show* image)
         [:ul {:class "card-action-links"}
          [:li {:class "card-action-link"}
           [:a {:href (v/edit-path req :images image)}
            "Edit Image"]]
          [:li {:class "card-action-link"}
           (f/form-to [:delete (v/destroy-path req :images image)]
                      (f/submit-button {:name "submit" :class "btn btn-secondary"} "Delete Image"))]]
         [:a {:href "/library/artefacts/images"} "Go Back"]))

(defn edit-form* [req image]
  (f/form-to {:enctype "multipart/form-data"}
             [:put (v/update-path req :images image)]
             [:div {:class "field"}
              (f/hidden-field :type "image_artefact")]
             [:div {:class "field"}
              (f/label :image-file "Image File:")
              (f/file-upload :image-file)]
             [:div {:class "actions"}
              (f/submit-button {:name "submit"} "Save")]))

(defn edit [req image]
  (l/app "Edit Image Artefact"
         (header req "Edit Image Artefact"
                 [{:path (v/index-path req :library)
                   :text "Library"}
                  {:path (v/index-path req :images)
                   :text "Images"}])
         (show* image)
         (edit-form* req image)))
