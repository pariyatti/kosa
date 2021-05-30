(ns kosa.mobile.today.looped.txt
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [kuti.support.collections :refer [merge-kvs]]))

(defn split-file [txt]
  (str/split txt #"~"))

(defprotocol Ingester
  (attr-for [this kw])
  (entry-attr [this])
  (human-name [this])
  (parse [this txt lang])
  (find-existing [this entry])
  (db-insert* [this entry])
  (citations [this entry])
  (download-attachments! [this lang entry]))

(defn db-insert! [ingester words-of-buddha]
  (let [entry-kw (entry-attr ingester)
        translations-kw (attr-for ingester :translations)]
    (if-let [existing (find-existing ingester words-of-buddha)]
      (let [merged (merge-kvs (translations-kw existing)
                              (translations-kw words-of-buddha))]
        (if (= merged (translations-kw existing))
          (log/info (format "Duplicate entry ignored: %s" (entry-kw words-of-buddha)))
          (do
            (log/info (format "Merging translations: %s" (entry-kw words-of-buddha)))
            (db-insert* ingester
                        (merge existing
                               {translations-kw merged}
                               (citations ingester words-of-buddha))))))
      (db-insert* ingester words-of-buddha))))

(defn ingest [ingester f lang]
  (let [human (human-name ingester)
        attach! (partial download-attachments! ingester lang)
        insert! (partial db-insert! ingester)]
    (log/info (format "%s TXT: started ingesting file '%s' for lang '%s'" human f lang))
    (let [entries (parse ingester (slurp f) lang)
          entry-count (count entries)]
      (log/info (format "Processing %s %s from TXT." entry-count human))
      (doseq [[n entry] (map-indexed #(vector %1 %2) entries)]
        (log/info (format "Attempting insert of %s / %s" (+ n 1) entry-count))
        (-> entry
            attach!
            insert!)))
    (log/info (format "%s TXT: done ingesting file '%s' for lang '%s'" human f lang))))
