(ns kosa-crux.views.layout
  (:use [hiccup.page :only (html5 include-css include-js)]))

(defn application [title & content]
  (html5 {:lang "en"}
         [:head
          [:title title]
          (include-css "/css/main.css")
          ;; (include-js "js/script.js")

          [:body
           [:div {:class "container"} content ]]]))
