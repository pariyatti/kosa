(ns kuti.support.strings
  (:require [clojure.string :as str]))

(defn slice [s start end]
  (if (< (.length s) end)
    (subs s start)
    (subs s start end)))

(defn trim!
  "Trims non-standard whitespace."
  [x]
  (-> x
      (str/replace (re-pattern (str \u00A0 "$")) "")
      (str/replace (re-pattern (str "^" \u00A0)) "")
      (str/trim)))
