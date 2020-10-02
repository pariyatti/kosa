(ns kosa-crux.layouts.shared.head
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]))

(defn render []
  (hc/html
   ;; (h/include-js "js/script.js")
   (hp/include-css "/css/main.css")
   (hp/include-css "/css/clr-icons.min.css")
   (hp/include-js "/js/custom-elements.min.js")
   (hp/include-js "/js/clr-icons.min.js")))

  ;; TODO: from rails --
  ;; <%= csrf_meta_tags %>
  ;; <%= csp_meta_tag %>

  ;; <%= stylesheet_link_tag 'application', media: 'all', 'data-turbolinks-track': 'reload' %>
  ;; <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
  ;; <%= javascript_pack_tag 'application', 'data-turbolinks-track': 'reload' %>
  ;; <%= javascript_pack_tag 'clarity_icons', 'data-turbolinks-track': 'reload' %>
