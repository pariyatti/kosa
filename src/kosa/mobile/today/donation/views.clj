(ns kosa.mobile.today.donation.views
  (:require [hiccup2.core :as h]
            [hiccup.form :as f]
            [kuti.storage :as storage]
            [kosa.layouts.mobile :as p]
            [kosa.views :as v]))

(defn show-index-preview [card]
  [:div {:class "card-index-content flex"}
   [:table
    [:tr
     [:td "Title:"]
     [:td (:donation/title card)]]
    [:tr
     [:td "Text:"]
     [:td (:donation/text card)]]
    [:tr
     [:td "Button:"]
     [:td (:donation/button card)]]
    ;; [:tr
    ;;  [:td "Image:"]
    ;;  [:td [:div [:img {:src (storage/url (:donation/image-attachment card))
    ;;                    :width "128"
    ;;                    :height "128"}]]]]
    ]
   [:ul {:class "card-action-links"}
    [:li {:class "card-action-link"} "Show"]
    [:li {:class "card-action-link"} "Edit"]
    [:li {:class "card-action-link"} "Destroy"]]])

(defn index [req cards]
  (p/app req "Donation Card Index"
   [:p {:id "notice"}
    "&lt;%= notice %&gt;"]
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/index-path req :mobile)}
      [:clr-icon {:shape "grid-view" :size "24"}]
      "&nbsp;Back to Mobile Admin"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "Donation Cards"]
     [:a {:href (v/new-path req :donations)}
      [:clr-icon {:shape "plus-circle" :size "24"}]
      "&nbsp;Create Donation Card"]]]
   (for [card cards]
     (show-index-preview card))))

(defn show* [card]
  (h/html
   [:table
    [:tr
     [:td "Title:"]
     [:td (:donation/title card)]]
    [:tr
     [:td "Text:"]
     [:td (:donation/text card)]]
    [:tr
     [:td "Button:"]
     [:td (:donation/button card)]]
    ;; [:tr
    ;;  [:td "Image:"]
    ;;  [:td [:div [:img {:src (storage/url (:donation/image-attachment card))
    ;;                    :width "128"
    ;;                    :height "128"}]]]]
    ]))

(defn show [req card]
  (p/app req "Show Donation Card"
         (show* card)
         [:ul {:class "card-action-links"}
           [:li {:class "card-action-link"} "Edit"]
           [:li {:class "card-action-link"} "Destroy"]]
         [:a {:href (v/index-path req :donations)} "Go Back"]))

(defn new-form [req]
  [:form {:method "POST"
          :action (v/create-path req :donations)
          :enctype "multipart/form-data"}
   [:div {:class "field"}
    (f/hidden-field :kuti/type :donation)]
   [:a {:href "#"
        :onclick "document.getElementById('defaults').classList.toggle('form-defaults-hidden');"}
    "Show / Hide Defaults"]
   [:div#defaults {:class "form-defaults-hidden"}
    [:div {:class "field"}
     (f/label :donation/header "Header")
     (f/text-field :donation/header "Donation")]]

   [:div {:class "field"}
    (f/label :donation/title "Title")
    (f/text-field :donation/title)]
   [:div {:class "field"}
    (f/label :donation/text "Text")
    (f/text-area :donation/text)]
   ;; [:div {:class "field"}
   ;;  [:div {:id "imagesearch"}
   ;;   [:p "If you're seeing this message, that means you haven't yet compiled your ClojureScript!"]]]
   [:div {:class "field"}
    (f/label :donation/button "Button Text")
    (f/text-field :donation/button)]
   [:div {:class "actions"}
    (f/submit-button {:name "submit"} "Save")]])

(defn new [req]
  (p/app req "New Donation Card"
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/index-path req :mobile)}
      "Back to Mobile Admin"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "New Donation Card"]]]
   [:div {:class "form-and-preview flex row"}
    (new-form req)]))
