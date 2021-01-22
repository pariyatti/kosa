(ns kosa.cli
    (:require [clojure.set :as set]
              [clojure.tools.cli :as tcli]
              [clojure.string :as str]))

(def ^:private cli-options
  [["-f" "--config-file FILE" "Path to configuration file"]
   ["-h" "--help" "Print this help message"]
   ["-R" "--routes" "Print HTTP routes"]
   ["-m" "--migrate" "Run migrations"]
   ["-r" "--rollback" "Rollback the last migration"]
   ["-s" "--start" "Start the server"]])

(defn- operational-modes [options]
  (set/intersection #{:help :routes :migrate :rollback :start} (into #{} (keys options))))

(defn parse [args]
  (tcli/parse-opts args cli-options))

(defn error-message [{:keys [options]}]
  (cond
    (not= 1 (count (operational-modes options)))
    "Should be invoked with exactly one of -h -R -r -m -s"

    (not (or (:config-file options) (:help options)))
    "Missing required option -f"

    :else nil))

(defn operational-mode [{:keys [options]}]
  (first (operational-modes options)))

(defn help-message [{:keys [summary]}]
  (str
   (str/join "\n\n" ["Pariyatti Kosa"
                     "Use only one option of -s or -h at once"
                     summary])
   "\n"))
