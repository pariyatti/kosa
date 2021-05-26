(ns dev.alias
  (:require [kosa.config :as config]
            [clojure.tools.logging :as log]
            [kosa.mobile.today.looped-pali-word.txt :as pali-txt]
            [kosa.mobile.today.looped-words-of-buddha.txt :as buddha-txt]
            [dev.repl]))

(defn ingest-txt-pali!
  "Ingest a Pali Word TXT file."
  []
  (log/info "Starting server before ingesting pali words...")
  (dev.repl/start!)
  (log/info "Ingesting pali words ... don't forget to start server first.")
  (doseq [txt (-> config/config :txt-feeds :pali-word)]
    (pali-txt/ingest (:file txt) (:language txt)))
  (dev.repl/stop!))

(defn ingest-txt-buddha!
  "Ingest a Words of Buddha TXT file."
  []
  (log/info "Starting server before ingesting words of buddha...")
  (dev.repl/start!)
  (log/info "Ingesting words of buddha ... don't forget to start server first.")
  (doseq [txt (-> config/config :txt-feeds :words-of-buddha)]
    (buddha-txt/ingest (:file txt) (:language txt)))
  (dev.repl/stop!))
