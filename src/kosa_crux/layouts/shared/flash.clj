(ns kosa-crux.layouts.shared.flash)

(defn render [flashes]
  [:div#flash
   (for [[k v] flashes]
     [:div {:class (format "flash %s" k)}
      v])])
