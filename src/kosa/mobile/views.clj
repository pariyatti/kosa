(ns kosa.mobile.views
  (:require [hiccup.core :as h]
            [hiccup.form :as f]
            [kosa.layouts.mobile :as p]
            [kosa.views :as v]))

(defn index [req]
  (p/app "Home"
         [:div {:class "section-all-cards"}
          [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Mobile App"]
            [:span {:class "page-subtitle"} "Use the links below to create and publish content to the Pariyatti mobile app."]]]
          [:ul {:class "card-types"}
           [:li {:class "card-type"}
            [:h3 "Overlay Inspiration Card"]
            [:p "An overlay inspiration card has an image and a text. This card is mainly used for quotes"]
            ;; "&lt;%= link_to &#39;Manage Cards&#39;, cards_overlay_inspiration_cards_path,class: &#39;link&#39;%&gt;\n            &lt;%= link_to &#39;Create Card&#39;, new_cards_overlay_inspiration_card_path, class: &#39;btn btn-primary&#39;%&gt;"
            ]

           [:li {:class "card-type"}
            [:h3 "Stacked Inspiration Card"]
            [:p "Same as the Overlay card, but the text and image in this card are placed one below the other"]
            [:a.link {:href (v/index-path req :stacked-inspirations)}
             "Manage Cards"]
            [:a.btn.btn-primary {:href (v/new-path req :stacked-inspirations)}
             "Create Card"]]

           [:li {:class "card-type"}
            [:h3 "Pali Word Card"]
            [:p "This card is used to display a Pali word and its translatation for a selected language"]
            [:a.link {:href (v/index-path req :pali-words)}
             "Manage Cards"]
            [:a.btn.btn-primary {:href (v/new-path req :pali-words)}
             "Create Card"]]]

          [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Import Data"]
            [:span {:class "page-subtitle"} "Import card data directly from raw files."]]]

          [:ul {:class "card-types"}
           [:li {:class "card-type"}
            [:h3 "Daily Words of the Buddha"]
            [:p ""]
            [:a.btn.btn-primary {:href (v/new-path req :import)}
             "Import TXT File"]]]]))
