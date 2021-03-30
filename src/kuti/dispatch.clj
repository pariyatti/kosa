(ns kuti.dispatch
  (:require [reitit.core]
            [fancy.table :as fancy]))

(defn pluralize [s]
  (str s "s"))

(defn singularize [s]
  (clojure.string/replace s #"s$" ""))

(defmacro resource [kw]
  (let [k (name kw)
        route (fn [& parts] (clojure.string/join "/" parts))

        name-kw #(keyword (name (ns-name *ns*)) (str k "-" %))

        singular (singularize k)
        handler #(symbol (str singular "-handler") %)

        spec #(keyword "entity" (str singular "-request"))]
    `[[~(route k) {:name    ~(name-kw "index")
                   :aliases [~(name-kw "create")]
                   :get     ~(handler "index")
                   :post    (~'wrap-spec-validation ~(spec) ~(handler "create"))}]
      [~(route k "new") {:name ~(name-kw "new")
                         :get  ~(handler "new")}]
      [~(route k ":id") {:name    ~(name-kw "show")
                         :aliases [~(name-kw "update") ~(name-kw "destroy")]
                         :get     ~(handler "show")
                         :put ~(handler "update")
                         :delete  ~(handler "destroy")}]
      [~(route k ":id" "edit") {:name ~(name-kw "edit")
                                :get  ~(handler "edit")}]]))

(defn flatvec [coll]
  (vec (mapcat seq coll)))

(defmacro resources [& kws]
  `(flatvec (vector ~@(for [kw# kws]
                     `(kuti.dispatch/resource ~kw#)))))

(defn fn-name [fn]
  (when fn
    (-> fn class str
        (clojure.string/replace #"\$" "/")
        (clojure.string/replace #"class " ""))))

(defn print-handler [c verb]
  (-> c second (get verb) :handler fn-name))

(defn print-route [c verb]
  {:verb verb
   :url (first c)
   :action (print-handler c verb)})

(defn print-url-cluster [c]
  (->> [:get :post :put :delete]
       (map (partial print-route c))
       (filter #(get % :action))))

(defn print-routes* [r]
  (->> r
       (map print-url-cluster)
       (apply concat)))

(defn print-routes [router verbose]
  (let [routes (reitit.core/routes router)]
    (if verbose
      (clojure.pprint/pprint routes)
      (fancy/print-table [:verb :url :action]
                         (print-routes* routes)))))
