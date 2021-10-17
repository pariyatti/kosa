(ns kosa.views
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

(defn pluralize [s]
  (-> s
      name
      ;; TODO: perhaps we'll pluralize in the future for API symmetry with Rails
      ;;       but for now simply using the plural everywhere seems more consistent.
      identity))

(defn root-route-ns [router]
  (let [kw (->> (r/routes router)
                (keep #(-> % second :name))
                (take 1))]
    (-> kw first namespace)))

(defn qualify [req type action]
  (keyword (str (root-route-ns (:reitit.core/router req))
                "/"
                (pluralize type)
                "-"
                (name action))))

(defn path*
  ([req type action]
   (path-for req (qualify req type action)))
  ([req type action obj]
   (path-for req (qualify req type action) (:xt/id obj))))

(defn index-path   [req type] (path* req type :index))
(defn create-path  [req type] (path* req type :create))
(defn new-path     [req type] (path* req type :new))
(defn show-path    [req type obj] (path* req type :show obj))
(defn update-path  [req type obj] (path* req type :update obj))
(defn destroy-path [req type obj] (path* req type :destroy obj))
(defn edit-path    [req type obj] (path* req type :edit obj))
