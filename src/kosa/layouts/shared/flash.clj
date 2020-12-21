(ns kosa.layouts.shared.flash
  (:require [hiccup.core :as hc]))

(defn render [flashes]
  (hc/html
   [:div#flash
    (for [[k v] flashes]
      [:div {:class (format "flash %s" k)}
       v])]))
