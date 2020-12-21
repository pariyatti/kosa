(ns kutis.dispatch)

(defn pluralize [s]
  (str s "s"))

(defn singularize [s]
  (clojure.string/replace s #"s$" ""))

(defmacro resources [kw]
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
                         :put     ~(handler "update")
                         :delete  ~(handler "destroy")}]
      [~(route k ":id" "edit") {:name ~(name-kw "edit")
                                :get  ~(handler "edit")}]]))
