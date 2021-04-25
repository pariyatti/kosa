(ns kuti.support.strings)

(defn slice [s start end]
  (if (< (.length s) end)
    (subs s start)
    (subs s start end)))
