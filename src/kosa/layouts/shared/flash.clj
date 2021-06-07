(ns kosa.layouts.shared.flash
  (:require [hiccup2.core :as h]))

(defn render [flashes]
  (h/html
   [:div#flash
    (for [[k v] flashes]
      [:div {:class (format "flash %s" k)}
       v])]))
