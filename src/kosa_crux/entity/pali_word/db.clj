(ns kosa-crux.entity.pali-word.db
  (:refer-clojure :exclude [list])
   (:require [kosa-crux.crux :as crux]))

(defn list []
  (let [list-pali-words-query {:find  '[e]
                               :where '[[e :type "pali_word"]]}]
    (crux/query list-pali-words-query)))
