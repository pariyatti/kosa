(ns dev.alias
  (:require [kosa.config :as config]
            [clojure.tools.logging :as log]
            [kosa.mobile.today.looped-pali-word.txt :as txt]
            [dev.repl]))

(defn ingest-txt-pwad!
  "Ingest a Pali Word TXT file."
  []
  (log/info "Starting server before ingesting pali words...")
  (dev.repl/start!)
  (log/info "Ingesting pali words ... don't forget to start server first.")
  (doseq [txt (-> config/config :txt-feeds :pali-word)]
    (txt/ingest (:file txt) (:language txt)))
  (dev.repl/stop!))
