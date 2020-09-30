(ns kosa-crux.publisher.entity.pali-word.handler
  (:refer-clojure :exclude [list])
  (:require [ring.util.response :as resp]
            [kosa-crux.publisher.entity.pali-word.db :as pali-word-db]
            [kosa-crux.publisher.entity.pali-word.views :as views]))

(defn index [_request]
  (let [cards (pali-word-db/list)]
    (resp/response
     (views/index cards))))

(defn new [_request]
  (resp/response
   (views/new)))

(defn create [{:keys [params]}]
  (let [tx (pali-word-db/put (assoc params :published-at (java.util.Date.)))
        ;; card (pali-word-db/get (:crux.db/id tx)) -- this isn't really a thing we can do (the card might not be on disk yet)
        ]
    ;; TODO: this conditional needs to validate params rather than asserting on db/put
    ;; TODO: additionally, this redirect really needs to busy-wait until the card is on disk... otherwise
    ;;       we always get "Card not found in Crux." on the first load of that page. :( -sd
    (if tx
      (resp/redirect (format "/publisher/today/pali_word_card/%s" (:crux.db/id tx)))
      (resp/response
       (str "It looks like your card wasn't saved? -- " tx)))))

(defn show [{:keys [route-params]}]
  (let [card (pali-word-db/get (:id route-params))]
    (if card
      (resp/response (views/show card))
      (resp/response "Card not found in Crux."))))

(defn list [_request]
  (resp/response
   (pali-word-db/list)))
