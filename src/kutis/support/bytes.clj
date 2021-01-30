(ns kutis.support.bytes)

(defn str->byte-array [s]
  (bytes (byte-array (map (comp byte int) s))))
