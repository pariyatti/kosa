(ns kosa.mobile.today.pali-word.views
  (:require [clojure.string :as str]
            [hiccup2.core :as h]
            [hiccup.form :as f]
            [kosa.config :as config]
            [kosa.layouts.mobile :as p]
            [kosa.views :as v]))

(defn show-index-preview [card]
  [:div {:class "card-index-content flex"}
   [:table
    [:tr
     [:td "Pali Word:"]
     [:td (:pali-word/pali card)]]
    (for [t (:pali-word/translations card)]
      (when-not (str/blank? (second t))
        [:tr
         [:td (first t)]
         [:td (second t)]]))]
   [:ul {:class "card-action-links"}
    [:li {:class "card-action-link"} "Show"]
    [:li {:class "card-action-link"} "Edit"]
    [:li {:class "card-action-link"} "Destroy"]]])

(defn index [req cards]
  (p/app req "Pali Word Card Index"
   [:p {:id "notice"}
    "&lt;%= notice %&gt;"]
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/index-path req :mobile)}
      [:clr-icon {:shape "grid-view" :size "24"}]
      "&nbsp;Back to Mobile Admin"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "Pali Cards"]
     [:a {:href (v/new-path req :pali-words)}
      [:clr-icon {:shape "plus-circle" :size "24"}]
      "&nbsp;Create Pali Word Card"]]]
   (for [card cards]
     (show-index-preview card))))

(defn show* [card]
  (h/html
   [:table
    [:tr
     [:td "Pali Word:"]
     [:td (:pali-word/pali card)]]
    (for [t (:pali-word/translations card)]
      [:tr
       [:td (first t)]
       [:td (second t)]])]))

(defn show [req card]
  (p/app req "Show Pali Word Card"
         (show* card)
         [:ul {:class "card-action-links"}
           [:li {:class "card-action-link"} "Edit"]
           [:li {:class "card-action-link"} "Destroy"]]
         [:a {:href (v/index-path req :pali-words)} "Go Back"]))

(defn new-form [req]
  (f/form-to [:post (v/create-path req :pali-words)]
             [:div {:class "field"}
              (f/hidden-field :kuti/type :pali-word)]
             [:a {:href "#"
                  :onclick "document.getElementById('defaults').classList.toggle('form-defaults-hidden');"}
              "Show / Hide Defaults"]
             [:div#defaults {:class "form-defaults-hidden"}
              [:div {:class "field"}
               (f/label :pali-word/header "Header")
               (f/text-field :pali-word/header "Pali Word")]]
             [:div {:class "field"}
              (f/label :pali-word/pali "Pali")
              (f/text-field :pali-word/pali)]

             [:div#translations-list
              (for [lang (:supported-languages config/config)]
                [:div
                 [:div {:class "field language"}
                  (f/text-field {:readonly "readonly"} :language lang)]
                 [:div {:class "field translation"}
                  (f/text-field :translation)]])]

             ;; TODO: include these fields also
;; [:div {:class "field"} "&lt;%= form.label :audio_file, &quot;Audio clip to upload:&quot; %&gt;\n    &lt;%= form.file_field :audio_file %&gt;"]
;; [:div {:class "field"} "&lt;%= form.label :language %&gt;\n    &lt;%= form.text_field :language %&gt;"]

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
  (p/app req "New Pali Word Card"
   [:div {:class "page-heading"}
    [:div {:class "breadcrumb"}
     [:a {:href (v/index-path req :mobile)}
      "Back to Mobile Admin"]]
    [:div {:class "header-and-link flex"}
     [:h1 {:class "page-header"} "New Pali Word Card"]]]
   [:div {:class "form-and-preview flex row"}
    (new-form req)
    ;; (show card) ;; TODO: requires javascript to do anything meaningful
    ]))
