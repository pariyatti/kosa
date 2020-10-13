(defproject kosa-crux "0.1.0-SNAPSHOT"
  :description "The Pariyatti Library"
  :url "http://github.com/pariyatti/kosa-crux"
  :license {:name "GNU Affero General Public License 3.0"
            :url  "https://www.gnu.org/licenses/agpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 ;; system:
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [mount "0.1.16"]
                 [tolitius/mount-up "0.1.3"]
                 [aero "1.1.6"]
                 ;; web:
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-json "0.5.0"]
                 [ring-logger "1.0.1"]
                 [metosin/reitit "0.5.6"]
                 [metosin/muuntaja "0.6.7"]
                 [hiccup "1.0.5"]
                 ;; data:
                 [juxt/crux-core "20.09-1.12.1-beta" :exclusions [org.slf4j/slf4j-api]]
                 [juxt/crux-rocksdb "20.09-1.12.1-beta" :exclusions [org.slf4j/slf4j-api]]]
  :plugins [[lein-scss "0.3.0"]]

  :scss {:builds
         {:development {:source-dir "resources/scss/"
                        :dest-dir   "resources/public/css/"
                        :executable "sass"
                        :args       ["--style" "expanded"]}
          :production {:source-dir  "resources/scss/"
                       :dest-dir    "resources/public/css/"
                       :executable  "sass"
                       :args        ["--style" "compressed"]
                       :jar         true}}}

  :main ^:skip-aot kosa-crux.core
  :target-path "target/%s"

  :repl-options {:init-ns dev.repl}
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev     {:dependencies [[org.clojure/test.check "1.1.0"]]}})
