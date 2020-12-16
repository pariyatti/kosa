(ns kosa-crux.library.artefacts.image.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa-crux.views :as v]
            [kosa-crux.layouts.library :as l]))

(defn show* [image]
  (h/html
   [:table
    [:tr
     [:td "Image Preview:"]
     [:td [:img {:src (:url image)}]]]
    [:tr
     [:td "URL:"]
     [:td (:url image)]]
    [:tr
     [:td "Original URL:"]
     [:td (:original-url image)]]]))

(defn show [req image]
  (l/app "Show Image Artefact"
         (show* image)
         [:ul {:class "card-action-links"}
          [:li {:class "card-action-link"} "Images cannot be edited"]
          [:li {:class "card-action-link"}
           (f/form-to [:delete (v/path-for req :kosa-crux.routes/image-destroy (:crux.db/id image))]
                      (f/submit-button {:name "submit"} "Delete Image"))]]
         [:a {:href "/library/artefacts/images"} "Go Back"]))

(defn new-form* [req]
  ;; TODO: create an equivalent to `f/form-to` for multipart form data
  [:form {:method "POST"
          :action (v/path-for req :kosa-crux.routes/image-create)
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
         [:div {:class "page-heading"}
          [:div {:class "breadcrumb"}
           [:a {:href (v/path-for req :kosa-crux.routes/library)}
            "Back to Library"]]
          [:div {:class "header-and-link flex"}
           [:h1 {:class "page-header"} "New Image Artefact"]]]
         [:div {:class "form-and-preview flex row"}
          (new-form* req)]))

(defn index [req images]
  (l/app "Image Artefacts"
         [:div {:class "page-heading"}
          [:div {:class "breadcrumb"}
           [:a {:href (v/path-for req :kosa-crux.routes/library)}
            [:clr-icon {:shape "grid-view" :size "24"}]
            "&nbsp;Back to Library"]]
          [:div {:class "header-and-link flex"}
           [:h1 {:class "page-header"} "Image Artefacts"]
           [:a {:href (v/path-for req :kosa-crux.routes/image-new)}
            [:clr-icon {:shape "plus-circle" :size "24"}]
            "&nbsp;Create Image Artefact"]]]

         [:div {:class "section-all-cards"}
          (for [img images]
            [:div
             [:div [:img {:src (:url img) :width "128" :height "128"}]]
             [:a {:href (v/path-for req :kosa-crux.routes/image-show (:crux.db/id img))}
              [:div (:url img)]]])]))
