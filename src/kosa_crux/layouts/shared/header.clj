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
             :height "40"}]]
     [:a {:href home-url}
      [:span title]]]

    [:div {:class "header-user"}
     [:p {:class "user-email"}
      [:a {:href other-home-url} (str "Switch to " other-title)]
      [:span "&nbsp; | &nbsp;"]
      [:div.menu-wrapper
       [:input {:id "menu-check" :class "menu"
                :type "checkbox" :name "menu"}]
       [:label {:class "menu btn btn-secondary" :for "menu-check"}
        "zig@pariyatti.org"]
       [:ul {:class "submenu"}
        [:li [:a {:href "#"} "TODO: Settings"]]
        [:li [:a {:href "#"} "TODO: Change Passwored"]]
        [:li [:a {:href "#"} "TODO: Login/Logout"]]]]]]]))
