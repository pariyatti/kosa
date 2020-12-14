(ns kosa-crux.layouts.library
  (:require [hiccup.page :as h]
            [kosa-crux.layouts.shared.head :as head]
            [kosa-crux.layouts.shared.header :as header]
            [kosa-crux.layouts.shared.flash :as flash]
            [kosa-crux.layouts.shared.footer :as footer]))

(defn app [title & content]
  (h/html5 {:lang "en"
            :encoding "UTF-8"}
           (head/render
            [:title (str "Pariyatti Library - " title)])

           [:body
            (header/render "Pariyatti Library" "/library"
                           "Pariyatti Mobile" "/publisher")
            [:div {:class "main-container"}
           ;; TODO: pass a flash map
             (flash/render {})
             content]
            (footer/render)]))
