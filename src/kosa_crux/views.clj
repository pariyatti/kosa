(ns kosa-crux.views
  (:require
   [reitit.core :as r]))

(defn path-for
  "Naively assumes someone has attached a router (from `reitit.ring/get-router`) to the request."
  ([request name]
   (r/match->path (r/match-by-name (:router request) name)))
  ([request name id]
   (r/match->path (r/match-by-name (:router request) name {:id id}))))
