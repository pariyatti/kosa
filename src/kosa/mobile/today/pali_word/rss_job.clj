(ns kosa.mobile.today.pali-word.rss-job
  {:deprecated "0.1"}
  (:require [remus :refer [parse-url]]
            [clojure.tools.logging :as log]
            [kuti.support :refer [when-let*]]
            [kuti.support.time :as time]
            [kosa.config :as config]
            [kosa.mobile.today.pali-word.db :as db]
            [clojure.string])
  (:import [java.net URI]))

(def etag (atom ""))
(def last-modified (atom ""))

(defn response->etag
  "This is not actually the correct way to do this, but it is required
  because of this but: https://bz.apache.org/bugzilla/show_bug.cgi?id=45023
  Also, yikes: https://bz.apache.org/bugzilla/show_bug.cgi?id=45023#c26"
  [response]
  (clojure.string/replace (-> response :headers :etag) #"-gzip" ""))

(defn response->modified [response]
  (-> response :headers :last-modified))

(defn poll []
  (let [feeds (-> config/config :rss-feeds :pali-word)
        ;; NOTE: currently, the Pali Word feed only exists for English
        url (->> feeds
                 (filter #(= "eng" (:language %)))
                 first
                 :url)
        result (parse-url url
                          {:headers {"If-None-Match" @etag
                                     "If-Modified-Since" @last-modified}})
        feed (:feed result)
        response (:response result)
        length (:length response)]
    (when (> 0 length) ;; this will return 0 when skpping: 'If-None-Match' or 'If-Modified-Since'
      (throw (ex-info (str "Failed to retrieve RSS feed: " url) response)))
    (when (< 0 length)
      (reset! etag (response->etag response))
      (reset! last-modified (response->modified response))
      feed)))

(defn trim [pali]
  (-> pali
      (clojure.string/split #"Pariyatti")
      first
      (clojure.string/replace #"<br />" "")
      (clojure.string/trim)))

(defn split-pali-english [hyphenated]
  (let [[pali english] (clojure.string/split hyphenated #"—")
        pali (clojure.string/trim (or pali ""))
        english (clojure.string/trim (or english ""))]
    [pali english]))

(defn db-insert [pali-word]
  (let [existing (db/find-all :pali-word/original-pali
                       (:pali-word/original-pali pali-word))]
    (log/info "Pali Word RSS: attempting insert")
    (when (= 0 (count existing))
      (db/save! pali-word))))

(defn parse* [feed]
  (when-let* [entry (-> feed :entries first)
              published-date (-> entry :published-date)
              pali-html (-> entry :description :value)
              pali-english (trim pali-html)
              [pali english] (split-pali-english pali-english)
              original-url (:uri entry)]
    {:pali-word/pali pali
     :pali-word/translations [["eng" english]]
     :pali-word/original-pali pali-english
     :pali-word/original-url (URI. original-url)
     :pali-word/published-at (time/instant published-date)}))

(defn parse [feed]
  (log/info "#### parsing pali word RSS feed")
  (if-let [pali-word (parse* feed)]
    (db-insert pali-word)
    (throw (ex-info "Failed to parse RSS feed." feed))))

(defn run-job! [_]
  (log/info "#### running pali word RSS job")
  ;; Ignore the entire job if a feed isn't returned. `(poll)` throws an exception
  ;; if the feed fails and returns nil if there are no modifications since the
  ;; last time we checked the feed.
  (when-let [feed (poll)]
    (parse feed)))

;; testing if-none-match (2021-02-02):
;; curl -I "https://download.pariyatti.org/pwad/pali_words.xml" --header 'If-None-Match: "7d6-5ba5cb78530ae-gzip"'
