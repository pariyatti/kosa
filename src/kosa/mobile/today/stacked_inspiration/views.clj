(ns kosa.mobile.today.stacked-inspiration.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kutis.storage :as storage]
            [kosa.config :as config]
            [kosa.layouts.mobile :as p]
            [kosa.views :as v]))

(defn show-index-preview [card]
  [:div {:class "card-index-content flex"}
   [:table
    [:tr
     [:td "Text:"]
     [:td (:text card)]]
    [:tr
     [:td "Image:"]
     [:td [:div [:img {:src (storage/url (:image-attachment card)) :width "128" :height "128"}]]]]]
   [:ul {:class "card-action-links"}
    [:li {:class "card-action-link"} "Show"]
    [:li {:class "card-action-link"} "Edit"]
    [:li {:class "card-action-link"} "Destroy"]]])

(defn index [req cards]
  (p/app "Stacked Inspiration Card Index"
   [:p {:id "notice"}
    "&lt;%= notice %&gt;"]
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/index-path req :mobile)}
      [:clr-icon {:shape "grid-view" :size "24"}]
      "&nbsp;Back to Mobile Admin"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "Stacked Inspiration Cards"]
     [:a {:href (v/new-path req :stacked-inspirations)}
      [:clr-icon {:shape "plus-circle" :size "24"}]
      "&nbsp;Create Stacked Inspiration Card"]]]
   (for [card cards]
     (show-index-preview card))))

(defn show* [card]
  (h/html
   [:table
    [:tr
     [:td "Bookmarkable?"]
     [:td (:bookmarkable card)]]
    [:tr
     [:td "Shareable?"]
     [:td (:shareable card)]]
    [:tr
     [:td "Text:"]
     [:td (:text card)]]
    [:tr
     [:td "Image:"]
     [:td [:div [:img {:src (storage/url (:image-attachment card)) :width "128" :height "128"}]]]]]))

(defn show [req card]
  (p/app "Show Stacked Inspiration Card"
         (show* card)
         [:ul {:class "card-action-links"}
           [:li {:class "card-action-link"} "Edit"]
           [:li {:class "card-action-link"} "Destroy"]]
         [:a {:href (v/index-path req :stacked-inspirations)} "Go Back"]))

(defn new-form [req]
  [:form {:method "POST"
          :action (v/create-path req :stacked-inspirations)
          :enctype "multipart/form-data"}
   [:div {:class "field"}
    (f/hidden-field :card-type "stacked_inspiration")]
   [:a {:href "#"
        :onclick "document.getElementById('defaults').classList.toggle('form-defaults-hidden');"}
    "Show / Hide Defaults"]
   [:div#defaults {:class "form-defaults-hidden"}
    [:div {:class "field"}
     (f/label :bookmarkable "Bookmarkable?")
     (f/check-box :bookmarkable :checked)]
    [:div {:class "field"}
     (f/label :shareable "Shareable?")
     (f/check-box :shareable :checked)]
    [:div {:class "field"}
     (f/label :header "Header")
     (f/text-field :header "Stacked Inspiration")]]

   [:div {:class "field"}
    (f/label :text "Text")
    (f/text-field :text)]
   [:div {:class "field"}
    (f/label :file "Image File:")
    (f/file-upload :file)]
   [:div {:class "actions"}
    (f/submit-button {:name "submit"} "Save")]])

(defn new [req]
  (p/app "New Stacked Inspiration Card"
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/index-path req :mobile)}
      "Back to Mobile Admin"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "New Stacked Inspiration Card"]]]
   [:div {:class "form-and-preview flex row"}
    (new-form req)]))
