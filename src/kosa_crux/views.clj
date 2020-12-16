(ns kosa-crux.views
  (:require [reitit.core :as r]))

(defn replace-alias [router name]
  (let [alias (->> (r/routes router)
                   (keep #(-> % second :aliases))
                   (not-empty)
                   (first))]
    (if-let [aliased-to (get alias name)]
      aliased-to
      name)))

(defn path-for* [request path-name id matcher]
  (let [router (:router request)
        name (replace-alias router path-name)]
     (r/match->path (matcher router name id))))

(defn path-for
  "Naively assumes someone has attached a router (from `reitit.ring/get-router`) to the request."
  ([request path-name]
   (path-for* request path-name nil
              (fn [router name _] (r/match-by-name router name))))
  ([request path-name id]
   (path-for* request path-name id
              (fn [router name id] (r/match-by-name router name {:id id})))))
