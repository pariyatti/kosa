(ns kuti.dispatch.routing
  (:require [reitit.core :as r]))

(defn contains-val? [v value]
  (some #{value} v))

(defn fail-on-collision [name coll]
  (if (< 1 (count coll))
    (throw (Exception. (format "Alias '%s' is colliding." name)))
    coll))

(defn replace-alias [router name]
  (let [routes (r/routes router)
        route (->> routes
                   (keep #(-> % second))
                   (filter #(contains-val? (:aliases %) name))
                   (not-empty)
                   (fail-on-collision name)
                   (first))]
    (if-let [aliased-to (:name route)]
      aliased-to
      name)))

(defn path-for* [request path-name id matcher]
  (let [router (:reitit.core/router request)
        name (replace-alias router path-name)]
    (if-let [match (matcher router name id)]
      (r/match->path match)
      (throw (Exception. (format "Named route '%s' cannot be found." path-name))))))

(defn path-for
  "Naively assumes someone has attached a router (from `reitit.ring/get-router`) to the request."
  ([request path-name]
   (path-for* request path-name nil
              (fn [router name _] (r/match-by-name router name))))
  ([request path-name id]
   (path-for* request path-name id
              (fn [router name id] (r/match-by-name router name {:id id})))))

(defn host [req]
  (let [port (:server-port req)
        default-port (or (= 80 port) (= 443 port))]
    (format "%s://%s%s"
            (case port
              80 "http"
              443 "https"
              (name (:scheme req)))
            (:server-name req)
            (if default-port "" (str ":" port)))))

(defn url-for
  ([request]
   (host request))
  ([request path-name]
   (format "%s%s" (host request) (path-for request path-name)))
  ([request path-name id]
   (format "%s%s" (host request) (path-for request path-name id))))
