(ns kosa-crux.layouts.shared.head
  (:require [hiccup.page :as h]))

(defn render []
  ;; (h/include-js "js/script.js")
  (h/include-css "/css/main.css"))

  ;; TODO: from rails --
  ;; <%= csrf_meta_tags %>
  ;; <%= csp_meta_tag %>

  ;; <%= stylesheet_link_tag 'application', media: 'all', 'data-turbolinks-track': 'reload' %>
  ;; <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
  ;; <%= javascript_pack_tag 'application', 'data-turbolinks-track': 'reload' %>
  ;; <%= javascript_pack_tag 'clarity_icons', 'data-turbolinks-track': 'reload' %>
