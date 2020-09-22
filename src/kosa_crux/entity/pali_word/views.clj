(ns kosa-crux.entity.pali-word.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa-crux.views.layout :as layout]))

(defn show [card]
  (layout/application
   [:table
    [:tr
     [:td "Bookmarkable?"]
     [:td (:bookmarkable card)]]
    [:tr
     [:td "Shareable?"]
     [:td (:shareable card)]]
    [:tr
     [:td "Pali Word:"]
     [:td (:pali card)]]]
   [:ul {:class "card-action-links"}
    [:li {:class "card-action-link"} "Show"]
    [:li {:class "card-action-link"} "Edit"]
    [:li {:class "card-action-link"} "Destroy"]]))

(defn new-form []
  ;; TODO: use `path-for` to get URLs from the router
  (f/form-to [:post "/publisher/today/pali_word_card/create"]
             [:div {:class "field"}
              (f/hidden-field :card_type "pali_word")
              (f/hidden-field :bookmarkable true)
              (f/hidden-field :shareable true)]
             [:div {:class "field"}
              (f/label :pali "Pali")
              (f/text-field :pali)]
             [:div {:class "actions"}
              (f/submit-button {:name "submit"} "Save")])

  ;; TODO: signal errors to user
  ;; "&lt;% if card.errors.any? %&gt;"
  ;; [:div {:id "error_explanation"}
  ;;  [:h2 "&lt;%= pluralize(card.errors.count, &quot;error&quot;) %&gt; prohibited this card from being saved:"]
  ;;  [:ul "&lt;% card.errors.full_messages.each do |message| %&gt;"
  ;;   [:li "&lt;%= message %&gt;"]"&lt;% end %&gt;"]]

;; TODO: include these fields also
;; [:div {:class "field"} "&lt;%= form.label :audio_file, &quot;Audio clip to upload:&quot; %&gt;\n    &lt;%= form.file_field :audio_file %&gt;"]
;; [:div {:class "field"} "&lt;%= form.label :language %&gt;\n    &lt;%= form.text_field :language %&gt;"]
;; [:div {:class "field"} "&lt;%= form.label :translation %&gt;\n    &lt;%= form.text_field :translation %&gt;"]
)

(defn new []
  (layout/application
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     ;; TODO: use `path-for` to get URLs from the router
     [:a {:href "/publisher"}
      "Back to Publisher"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "New Pali Word Card"]]]
   [:div {:class "form-and-preview flex row"}
    (new-form)
    ;; (show card) ;; TODO: requires javascript to do anything meaningful
    ]))

(defn index [cards]
  (layout/application
   [:p {:id "notice"}
    "&lt;%= notice %&gt;"]
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     ;; TODO: use `path-for` to get URLs from the router
     [:a {:href "/publisher"}
      "Back to Publisher"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "Pali Cards"]
     ;; TODO: use `path-for` to get URLs from the router
     [:a {:href "/publisher/today/pali_word_card/new"}
      "Create Pali Word Card"]]]
   (for [card cards]
     [:div {:class "card-index-content flex"}
      (show card)])))
