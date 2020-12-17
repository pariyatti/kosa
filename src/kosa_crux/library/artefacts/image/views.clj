(ns kosa-crux.library.artefacts.image.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa-crux.views :as v]
            [kosa-crux.layouts.library :as l]))

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
    [:a {:href (v/path-for req :kosa-crux.routes/library)}
     [:clr-icon {:shape "grid-view" :size "24"}]
     "&nbsp;Back to Library"]]
   [:div {:class "header-and-link flex"}
    [:h1 {:class "page-header"} "Image Artefacts"]
    [:a {:href (v/path-for req :kosa-crux.routes/images-new)}
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
              [:a {:href (v/path-for req :kosa-crux.routes/images-show (:crux.db/id img))}
               [:div (:url img)]]])]]))

(defn new-form* [req]
  [:form {:method "POST"
          :action (v/path-for req :kosa-crux.routes/images-create)
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
                 [{:path (v/path-for req :kosa-crux.routes/library)
                   :text "Library"}
                  {:path (v/path-for req :kosa-crux.routes/images-index)
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
                 [{:path (v/path-for req :kosa-crux.routes/library)
                   :text "Library"}
                  {:path (v/path-for req :kosa-crux.routes/images-index)
                   :text "Images"}])
         (show* image)
         [:ul {:class "card-action-links"}
          [:li {:class "card-action-link"}
           [:a {:href (v/path-for req :kosa-crux.routes/images-edit (:crux.db/id image))}
            "Edit Image"]]
          [:li {:class "card-action-link"}
           (f/form-to [:delete (v/path-for req :kosa-crux.routes/images-destroy (:crux.db/id image))]
                      (f/submit-button {:name "submit" :class "btn btn-secondary"} "Delete Image"))]]
         [:a {:href "/library/artefacts/images"} "Go Back"]))

(defn edit-form* [req image]
  (f/form-to {:enctype "multipart/form-data"}
             [:put (v/path-for req :kosa-crux.routes/images-update (:crux.db/id image))]
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
                 [{:path (v/path-for req :kosa-crux.routes/library)
                   :text "Library"}
                  {:path (v/path-for req :kosa-crux.routes/images-index)
                   :text "Images"}])
         (show* image)
         (edit-form* req image)))
