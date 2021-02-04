(ns kosa.mobile.today.pali-word.rss-job
  (:require [remus :refer [parse-url]]
            [kutis.support :refer [when-let*]]
            [kosa.config :as config]
            [kosa.mobile.today.pali-word.db :as db]
            [clojure.string]))

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
  (let [url (-> config/config :rss-feeds :pali-word)
        result (parse-url url
                          {:headers {"If-None-Match" @etag
                                     "If-Modified-Since" @last-modified}})
        feed (:feed result)
        response (:response result)
        length (:length response)]
    (when (> 0 length) ;; this will return 0 when skpping: 'If-None-Match' or 'If-Modified-Since'
      (throw (ex-info (str "Failed to retrieve RSS feed: " url) response)))
    (when (< 0 length)
      (do
        (reset! etag (response->etag response))
        (reset! last-modified (response->modified response))
        feed))))

(defn trim [pali]
  (-> pali
      (clojure.string/split #"Pariyatti")
      first
      (clojure.string/replace #"<br />" "")
      (clojure.string/trim)))

(defn db-insert [pali original-url]
  ;; TODO: check for 'pali' and don't repeat inserts
  (db/put {:card-type "pali_word"
           :pali pali
           :original-url original-url
           :bookmarkable true
           :shareable true}))

(defn parse* [feed]
  (when-let* [entry (-> feed :entries first)
              pali-html (-> entry :description :value)
              pali (trim pali-html)
              original-url (:uri entry)]
    (db-insert pali original-url)))

(defn parse [feed]
  (if-not (parse* feed)
    (throw (ex-info "Failed to parse RSS feed." feed))))

(defn run-job! []
  ;; Ignore the entire job if a feed isn't returned. `(poll)` throws an exception
  ;; if the feed fails and returns nil if there are no modifications since the
  ;; last time we checked the feed.
  (when-let [feed (poll)]
    (parse feed)))

;; testing if-none-match (2021-02-02):
;; curl -I "https://download.pariyatti.org/pwad/pali_words.xml" --header 'If-None-Match: "7d6-5ba5cb78530ae-gzip"'
