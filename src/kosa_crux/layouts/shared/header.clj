(ns kosa-crux.layouts.shared.header
  (:require [hiccup.core :as hc]))

(defn render [title]
  (hc/html
   [:header {:class "header"}
    [:div {:class "header-logo"}
     [:a {:href "/publisher"}
      [:img {:src "/images/pariyatti-logo-256-solid.png"
             :width "40"
             :height "40"}]
      [:span title]]]
    [:div {:class "header-user"}
     [:p {:class "user-email"}
      "TODO: user email | Change Password Link | Switch Lib/Pub | Settings | Home"]
     [:div
      [:a {:href "/sign_out"}
       "TODO: Login/Logout"]]]]))
