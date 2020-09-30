(ns kosa-crux.publisher.entity.pali-word.db
  (:refer-clojure :exclude [list get])
  (:require [kosa-crux.crux :as crux]))

(defn list []
  (let [list-pali-words-query {:find  '[e]
                               :where '[[e :card-type "pali_word"]]}]
    (crux/query list-pali-words-query)))

(defn put [params]
  (let [db-params (select-keys params [:card-type :bookmarkable :shareable :pali])
        id        (crux/uuid)
        tx        (crux/put (assoc db-params :crux.db/id id))]
    (assoc tx :crux.db/id id)))

(defn get [id]
  (crux/get id))
