(ns kosa-crux.publisher.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa-crux.layouts.publisher :as p]))

(defn index []
  (p/app "Publisher"
         [:div {:class "section-all-cards"}
 [:div {:class "page-heading"}
  [:div {:class "header-and-link"}
   [:h1 {:class "page-header"} "Mobile App"]
   [:span {:class "page-sub-title"} "Use the links below to create and publish content to the Pariyatti mobile app."]]]
 [:ul {:class "card-types"}
  [:li {:class "card-type"}
   [:h3 "Overlay Inspiration Card"]
   [:p "An overlay inspiration card has an image and a text. This card is mainly used for quotes"]
   ;; "&lt;%= link_to &#39;Manage Cards&#39;, cards_overlay_inspiration_cards_path,class: &#39;link&#39;%&gt;\n            &lt;%= link_to &#39;Create Card&#39;, new_cards_overlay_inspiration_card_path, class: &#39;btn btn-primary&#39;%&gt;"
   ]
  [:li {:class "card-type"}
   [:h3 "Stacked Inspiration Card"]
   [:p "Same as the Overlay card, but the text and image in this card are placed one below the other"]
   ;; "&lt;%= link_to &#39;Manage Cards&#39;, cards_stacked_inspiration_cards_path,class: &#39;link&#39;%&gt;\n            &lt;%= link_to &#39;Create Card&#39;, new_cards_stacked_inspiration_card_path, class: &#39;btn btn-primary&#39;%&gt;"
   ]
  [:li {:class "card-type"}
   [:h3 "Pali Word Card"]
   [:p "This card is used to display a Pali word and its translatation for a selected language"]
   [:a.link {:href "/publisher/today/pali_word_cards"}
    "Manage Cards"]
   [:a.btn.btn-primary {:href "/publisher/today/pali_word_card/new"}
    "Create Card"]]]]))
