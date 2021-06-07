(ns kosa.mobile.today.words-of-buddha.rss-job
  {:deprecated "0.1"}
  (:require [remus :refer [parse-url]]
            [kosa.config :as config]))

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
  (let [url (-> config/config :rss-feeds :words-of-buddha)
        result (parse-url url
                          {:headers {"If-None-Match" @etag
                                     "If-Modified-Since" @last-modified}})
        feed (:feed result)
        response (:response result)
        length (:length response)
        ]
    ;; (when (> 0 length) ;; this will return 0 when skpping: 'If-None-Match' or 'If-Modified-Since'
    ;;   (throw (ex-info (str "Failed to retrieve RSS feed: " url) response)))
    (when (< 0 length)
      (reset! etag (response->etag response))
      (reset! last-modified (response->modified response))
      feed)
    ))

;; TODO: return to this and possibly use the RSS feed to synchronize the
;;       Daily Words with pariyatti.org? The actual data has to come directly
;;       from the text files, though, because some of them are out of order.
