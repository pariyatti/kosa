(ns kosa.mobile.today.pali-word.rss-parser
  (:require [remus :refer [parse-url]]
            [clojure.string]))

(def etag (atom ""))
(def last-modified (atom ""))

(defn response->etag [response]
  (clojure.string/replace (-> response :headers :etag) #"-gzip" ""))

(defn poll []
  ;; TODO: grab from config
  (let [url "https://download.pariyatti.org/pwad/pali_words.xml"
        result (parse-url url
                          {:headers {"If-None-Match" @etag
                                     "If-Modified-Since" @last-modified}})
        feed (:feed result)
        response (:response result)
        length (:length response)]
    (when (> 0 length) ;; this will return 0 when 'If-None-Match'
      (throw (ex-info (str "Failed to retrieve RSS feed: " url) response)))
    (when (< 0 length)
      (do
        (reset! etag (response->etag response))
        (reset! last-modified (-> response :headers :last-modified))
        feed))))

;; testing if-none-match (2021-02-02):
;; curl -I "https://download.pariyatti.org/pwad/pali_words.xml" --header 'If-None-Match: "7d6-5ba5cb78530ae-gzip"'
