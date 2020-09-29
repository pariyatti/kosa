(ns kosa-crux.publisher.entity.pali-word.db
  (:refer-clojure :exclude [list])
  (:require [kosa-crux.crux :as crux]))

(defn list []
  (let [list-pali-words-query {:find  '[e]
                               :where '[[e :card-type "pali_word"]]}]
    (crux/query list-pali-words-query)))

(defn put [params]
  ;; :params {:card-type pali_word, :bookmarkable value, :shareable value, :pali zig, :submit Save}
  (let [db-params (select-keys params [:card-type :bookmarkable :shareable :pali])
        ;; TODO: obviously we'll use real IDs later
        id        (:pali db-params)]
    (crux/put (assoc db-params :crux.db/id id))))
