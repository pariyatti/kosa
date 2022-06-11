(ns kosa.views
  (:require [reitit.core :as r]
            [kuti.dispatch.routing :as routing]))

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
   (routing/path-for req (qualify req type action)))
  ([req type action obj]
   (routing/path-for req (qualify req type action) (:xt/id obj))))

(defn index-path   [req type] (path* req type :index))
(defn create-path  [req type] (path* req type :create))
(defn new-path     [req type] (path* req type :new))
(defn show-path    [req type obj] (path* req type :show obj))
(defn update-path  [req type obj] (path* req type :update obj))
(defn destroy-path [req type obj] (path* req type :destroy obj))
(defn edit-path    [req type obj] (path* req type :edit obj))
