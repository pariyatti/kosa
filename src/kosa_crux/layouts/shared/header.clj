(ns kosa-crux.layouts.shared.header)

(defn render [title]
  [:header {:class "header"}
   [:div {:class "header-logo"}
    [:img {:src "/images/pariyatti-logo-256-solid.png"
           :width "40"
           :height "40"}]
    [:span title]]
   [:div {:class "header-user"}
    [:p {:class "user-email"}
     "TODO: user email | Change Password Link | Switch Lib/Pub | Settings | Home"]
    [:div
     [:a {:href "/sign_out"}
      "TODO: Login/Logout"]]]])
