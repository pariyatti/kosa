(ns kosa-crux.views
  (:require
   [reitit.core :as r]))

(defn path-for
  "Naively assumes someone has attached the reitit router from reitit.ring/get-router"
  [request name]
  (r/match->path (r/match-by-name (:router request) name)))
