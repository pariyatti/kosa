(ns kosa.auth.views
  (:require [kosa.layouts.auth :as p]
            [kosa.views :as v]))

(defn login [req]
  (p/app req "Login"
         [:div {:class "page-heading"}
           [:div {:class "header-and-link"}
            [:h1 {:class "page-header"} "Login"]
            [:span {:class "page-subtitle"} "You are logged out."]]]

         [:div {:class "form-and-preview flex row"}
          [:a.btn.btn-primary {:href (v/index-path req :mobile)}
          "Click to Login"]]))
