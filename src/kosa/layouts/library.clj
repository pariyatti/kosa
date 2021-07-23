(ns kosa.layouts.library
  (:require [hiccup.page :as h]
            [kosa.layouts.shared.flash :as flash]
            [kosa.layouts.shared.footer :as footer]
            [kosa.layouts.shared.head :as head]
            [kosa.layouts.shared.header :as header]))

(defn app [req title & content]
  ;; IANA cannot use 3-char language codes for English:
  ;; http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
  (h/html5 {:lang "en"
            :encoding "UTF-8"}
           (head/render
            [:title (str "Pariyatti Library - " title)])

           [:body
            (header/render req
                           "Pariyatti Library" "/library"
                           "Pariyatti Mobile Admin" "/mobile")
            [:div {:class "main-container"}
           ;; TODO: pass a flash map
             (flash/render {})
             content]
            (footer/render)]))
