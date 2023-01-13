(ns dev.alias
  (:require [kosa.config :as config]
            [clojure.tools.logging :as log]
            [kosa.mobile.today.looped-pali-word.txt :as pali-txt]
            [kosa.mobile.today.looped-words-of-buddha.txt :as buddha-txt]
            [kosa.mobile.today.looped-doha.txt :as doha-txt]
            [dev.repl]))

(defn ingest!
  "Ingest a TXT file."
  [conf card-type ingest-fn validate-fn]
  (log/info (format "Starting server before ingesting %s..." card-type))
  (dev.repl/start! (dev.repl/get-conf conf))
  (log/info (format "Ingesting %s..." card-type))
  (doseq [txt (-> config/config :txt-feeds (get card-type))]
    (ingest-fn (:file txt) (:language txt)))
  (validate-fn)
  (dev.repl/stop!))

(defn ingest-txt-pali!
  "Ingest a Pali Word TXT file."
  ([] (ingest-txt-pali! :dev))
  ([conf] (ingest! conf :pali-word pali-txt/ingest pali-txt/validate)))

(defn ingest-txt-buddha!
  "Ingest a Words of Buddha TXT file."
  ([] (ingest-txt-buddha! :dev))
  ([conf] (ingest! conf :words-of-buddha buddha-txt/ingest buddha-txt/validate)))

(defn ingest-txt-doha!
  "Ingest a Doha TXT file."
  ([] (ingest-txt-doha! :dev))
  ([conf] (ingest! conf :doha doha-txt/ingest doha-txt/validate)))
