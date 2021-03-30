(ns kuti.search)

(defn make-searchable [s]
  (as-> (clojure.string/split s #"-|_|~|=|\$|\{|\}|\.|\[|\]|\+") searchables
    (conj searchables s)
    (clojure.string/join " " searchables)))

(defn tag-searchables [e string]
  (let [old (:searchables e)
        built (make-searchable string)
        found (when old (clojure.string/includes? old built))
        new (-> old
                (str " " (when-not found built))
                clojure.string/trim)]
    (assoc e :searchables new)))
