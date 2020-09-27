(ns kosa-crux.routes
  (:require [ring.util.response :as resp]
            [bidi.ring]
            [kosa-crux.middleware :refer [wrap-spec-validation]]
            [kosa-crux.entity.pali-word.spec :as spec]
            [kosa-crux.entity.pali-word.handler :as pali-word-handler]))

(defn not-found [_request]
  (resp/not-found {:message "not-found"}))

(defn pong [_request]
  (resp/response "pong"))

(def routes
  ["/" [["ping" pong]
        ["css" (bidi.ring/->Files {:dir "resources/public/css"})
         ;; (bidi.ring/->Resources {:prefix "resources/public/css"})
         ]
        ["publisher/today/pali_word_cards" pali-word-handler/index]
        ["publisher/today/pali_word_card/new" pali-word-handler/new]
        ["publisher/today/pali_word_card/create" (wrap-spec-validation ::spec/pali-word-request pali-word-handler/create)]
        ["api/v1/today.json" pali-word-handler/list]
        ["" pali-word-handler/index]
        [true not-found]]])
