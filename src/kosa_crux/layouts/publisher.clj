(ns kosa-crux.layouts.publisher
  (:require [hiccup.page :as h]
            [kosa-crux.layouts.shared.head :as head]
            [kosa-crux.layouts.shared.header :as header]
            [kosa-crux.layouts.shared.flash :as flash]
            [kosa-crux.layouts.shared.footer :as footer]))

(defn app [title & content]
  (h/html5 {:lang "en"
            :encoding "UTF-8"}
           (head/render
            [:title title])

           [:body
            (header/render "Pariyatti Publisher")
            [:div {:class "main-container"}
           ;; TODO: pass a flash map
             (flash/render {})
             content]
            (footer/render)]))
