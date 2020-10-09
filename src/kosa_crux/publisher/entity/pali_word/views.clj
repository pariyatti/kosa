(ns kosa-crux.publisher.entity.pali-word.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa-crux.views :as v]
            [kosa-crux.layouts.publisher :as p]))

(defn show* [card]
  (h/html [:table
           [:tr
            [:td "Bookmarkable?"]
            [:td (:bookmarkable card)]]
           [:tr
            [:td "Shareable?"]
            [:td (:shareable card)]]
           [:tr
            [:td "Pali Word:"]
            [:td (:pali card)]]]))

(defn show [card]
  (p/app "Show Pali Word Card"
         (show* card)
         [:ul {:class "card-action-links"}
           [:li {:class "card-action-link"} "Edit"]
           [:li {:class "card-action-link"} "Destroy"]]
         [:a {:href "/publisher/today/pali_word_cards"} "Go Back"]))

(defn new-form [req]
  (f/form-to [:post (v/path-for req :kosa-crux.routes/pali-word-create)]
             [:div {:class "field"}
              (f/hidden-field :card-type "pali_word")]
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
               (f/text-field :header "Pali Word")]]
             [:div {:class "field"}
              (f/label :pali "Pali")
              (f/text-field :pali)]
             ;; TODO: include these fields also
;; [:div {:class "field"} "&lt;%= form.label :audio_file, &quot;Audio clip to upload:&quot; %&gt;\n    &lt;%= form.file_field :audio_file %&gt;"]
;; [:div {:class "field"} "&lt;%= form.label :language %&gt;\n    &lt;%= form.text_field :language %&gt;"]
;; [:div {:class "field"} "&lt;%= form.label :translation %&gt;\n    &lt;%= form.text_field :translation %&gt;"]

             [:div {:class "actions"}
              (f/submit-button {:name "submit"} "Save")])

  ;; TODO: signal errors to user
  ;; "&lt;% if card.errors.any? %&gt;"
  ;; [:div {:id "error_explanation"}
  ;;  [:h2 "&lt;%= pluralize(card.errors.count, &quot;error&quot;) %&gt; prohibited this card from being saved:"]
  ;;  [:ul "&lt;% card.errors.full_messages.each do |message| %&gt;"
  ;;   [:li "&lt;%= message %&gt;"]"&lt;% end %&gt;"]]

)

(defn new [req]
  (p/app "New Pali Word Card"
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/path-for req :kosa-crux.routes/publisher)}
      "Back to Publisher"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "New Pali Word Card"]]]
   [:div {:class "form-and-preview flex row"}
    (new-form req)
    ;; (show card) ;; TODO: requires javascript to do anything meaningful
    ]))

(defn index [req cards]
  (p/app "Pali Word Card Index"
   [:p {:id "notice"}
    "&lt;%= notice %&gt;"]
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/path-for req :kosa-crux.routes/publisher)}
      [:clr-icon {:shape "grid-view" :size "24"}]
      "&nbsp;Back to Publisher"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "Pali Cards"]
     [:a {:href (v/path-for req :kosa-crux.routes/pali-word-new)}
      [:clr-icon {:shape "plus-circle" :size "24"}]
      "&nbsp;Create Pali Word Card"]]]
   (for [card cards]
     [:div {:class "card-index-content flex"}
      (show* card)
      [:ul {:class "card-action-links"}
           [:li {:class "card-action-link"} "Show"]
           [:li {:class "card-action-link"} "Edit"]
           [:li {:class "card-action-link"} "Destroy"]]])))
