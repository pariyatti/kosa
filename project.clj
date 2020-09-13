(defproject kosa-crux "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [mount "0.1.16"]
                 [aero "1.1.6"]
                 [juxt/crux-core "20.09-1.11.0-beta"]
                 [juxt/crux-rocksdb "20.09-1.11.0-beta"]]
  :main ^:skip-aot kosa-crux.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
