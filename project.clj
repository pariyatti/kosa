(defproject kosa "0.1.0-SNAPSHOT"
  :description "The Pariyatti Library"
  :url "http://github.com/pariyatti/kosa"
  :license {:name "GNU Affero General Public License 3.0"
            :url  "https://www.gnu.org/licenses/agpl-3.0.en.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/algo.monads "0.1.6"]
                 [medley "1.3.0"]
                 ;; logging:
                 [fancy "0.2.3"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [ch.qos.logback/logback-core "1.2.3"]
                 [org.slf4j/slf4j-api "1.7.30"]
                 ;; system:
                 [mount "0.1.16"]
                 [tolitius/mount-up "0.1.3"]
                 [aero "1.1.6"]
                 [tick "0.4.30-alpha"]
                 [jarohen/chime "0.3.2"]
                 [babashka/fs "0.0.3"]
                 ;; networking:
                 [http-kit "2.5.3"]
                 [com.draines/postal "2.0.4"]
                 ;; web:
                 ;; [pro.juxt.clojars-mirrors.ring/ring-core "1.9.2"]
                 [ring/ring-core "1.9.2"]
                 [ring/ring-jetty-adapter "1.9.3"]
                 [ring/ring-json "0.5.1"]
                 [ring-logger "1.0.1"]
                 [metosin/reitit "0.5.6"]
                 [metosin/muuntaja "0.6.7"]
                 [metosin/jsonista "0.2.7"] ;; force version: reitit / muuntaja dep
                 [hiccup "2.0.0-alpha2"]
                 [remus "0.2.1"]
                 ;; data:
                 [buddy/buddy-core    "1.10.1"]
                 [buddy/buddy-auth    "3.0.1"]
                 [buddy/buddy-hashers "1.8.1"]
                 [pro.juxt.crux/crux-core        "1.17.1"]
                 [pro.juxt.crux/crux-rocksdb     "1.17.1"]
                 [pro.juxt.crux/crux-lucene      "1.17.1"]
                 [pro.juxt.crux/crux-http-server "1.17.1"]
                 [joplin.core "0.3.11"]
                 [org.pariyatti/joplin.crux "0.0.3"]

                 ;; clojurescript
                 [org.clojure/clojurescript "1.10.191"]
                 [org.clojure/core.async "1.3.610"]
                 ;; [reagent "1.0.0"] ;; conflicts with reagent-forms
                 [reagent-forms "0.5.23"]
                 [json-html "0.3.5"]
                 [lambdaisland/uri "1.4.54"] ;; uri is a fetch dep
                 [lambdaisland/fetch "0.0.23"]]

  :plugins [[lein-scss "0.3.0"]
            [lein-cljsbuild "1.1.8"]
            [reifyhealth/lein-git-down "0.4.0"]]

  :middleware [lein-git-down.plugin/inject-properties]
  :repositories [["public-github" {:url "git://github.com"}]]
  :git-down {org.pariyatti/joplin.crux {:coordinates pariyatti/joplin.crux}}

  :scss {:builds
         {:development {:source-dir "resources/scss/"
                        :dest-dir   "resources/public/css/"
                        :executable "sass"
                        :args       ["--style" "expanded"
                                     ;; ignore emacs' .#xyz.scss
                                     "--no-stop-on-error"]}
          :production {:source-dir  "resources/scss/"
                       :dest-dir    "resources/public/css/"
                       :executable  "sass"
                       :args        ["--style" "compressed"]
                       :jar         true}}}

  :cljsbuild {:builds
              [{:source-paths ["src-cljs"],
                :compiler
                {:output-dir "resources/public/cljs/",
                 :optimizations :none,
                 :output-to "resources/public/cljs/app.js",
                 :source-map true,
                 :pretty-print true}}]}

  :main ^:skip-aot kosa.core
  :target-path "target/%s"

  :aliases {"db-migrate"    ["run" "-m" "joplin.crux.alias/migrate"  "joplin/config.edn"]
            "db-seed"       ["run" "-m" "joplin.crux.alias/seed"     "joplin/config.edn"]
            "db-rollback"   ["run" "-m" "joplin.crux.alias/rollback" "joplin/config.edn"]
            "db-reset"      ["run" "-m" "joplin.crux.alias/reset"    "joplin/config.edn"]
            "db-pending"    ["run" "-m" "joplin.crux.alias/pending"  "joplin/config.edn"]
            "db-create"     ["run" "-m" "joplin.crux.alias/create"   "joplin/config.edn" "dev" "crux-dev"]
            "db-txt-pali"   ["run" "-m" "dev.alias/ingest-txt-pali!"]
            "db-txt-buddha" ["run" "-m" "dev.alias/ingest-txt-buddha!"]
            "db-txt-doha"   ["run" "-m" "dev.alias/ingest-txt-doha!"]}

  :repl-options {:init-ns dev.repl}
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"
                                  "--illegal-access=permit"]}
             :dev     {:dependencies [[org.clojure/test.check "1.1.0"]]
                       :resource-paths ["config/dev"]}}

  :test-selectors {:default     (complement :integration)
                   :integration :integration
                   :database    :database
                   :unit        (fn [m & _]
                                  (not (or (:integration m)
                                           (:database m))))
                   :all         (constantly true)})
