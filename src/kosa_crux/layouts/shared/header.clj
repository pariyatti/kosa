(ns kosa-crux.layouts.shared.header
  (:require [hiccup.core :as hc]))

(defn render [title home-url
              other-title other-home-url]
  (hc/html
   [:header {:class "header"}
    [:div {:class "header-logo"}
     [:a {:href home-url}
      [:img {:src "/images/pariyatti-logo-256-solid.png"
             :width "40"
             :height "40"}]
      [:span title]]]

    [:div {:class "header-user"}
     [:p {:class "user-email"}
      [:a {:href other-home-url} (str "Switch to " other-title)]
      [:span "&nbsp; | &nbsp;"]
      [:div.dropdown
       [:span [:a.dropdown {:href "#"} "zig@pariyatti.org"]]
       [:div.dropdown-content
        [:div "TODO: Settings"]
        [:div "TODO: Change Password"]
        [:div "TODO: Login/Logout"]]]

      [:div.menu-wrapper
       [:input {:id "menu-check" :class "menu"
                :type "checkbox" :name "menu"}]
       [:label {:class "menu" :for "menu-check"}
        "Menu"]
       [:ul {:class "submenu"}
        [:li [:a {:href "#"} "Item 1"]]
        [:li [:a {:href "#"} "Item 2"]]]]

      ]]]))
