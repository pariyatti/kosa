(ns kosa.api.status
  (:refer-clojure :exclude [get])
  (:require [clojure.tools.logging :as log]
            [kuti.storage.open-uri :as open-uri]
            [kuti.record :as record]
            [kuti.support.time :as time]
            [kuti.mailer :as mailer]
            [clojure.java.io :as io]))

(defn get []
  (let [test-url "http://download.pariyatti.org/dohas/001_Doha.mp3"
        test-res (try
                   (-> (open-uri/download-uri! test-url)
                       (clojure.core/get :tempfile)
                       (io/delete-file))
                   (catch clojure.lang.ExceptionInfo e
                     e))
        node-status (try
                      (record/status)
                      (catch Throwable e
                        e))
        mailer-status (try
                        (mailer/send-mail {:to "devnull@pariyatti.org"
                                           :subject "Kosa Status Check"
                                           :body "Kosa Status Check. Please Ignore this."})
                        (catch Throwable e
                          e))]

    {:timestamp (time/now)
     :mailer-status {:mailer-status (str mailer-status)
                     :mailer-ok (not (instance? Throwable mailer-status))}
     :xtdb-status (assoc node-status
                         :xtdb-ok (int? (:xtdb.kv/size node-status)))
     :pariyatti-status {:test-url test-url
                        :test-file test-res
                        :pariyatti-ok (not (instance? Throwable test-res))}}))
