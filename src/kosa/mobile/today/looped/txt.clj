(ns kosa.mobile.today.looped.txt
  (:require [clojure.string :as str]))

(defn split-file [txt]
  (str/split txt #"~"))
