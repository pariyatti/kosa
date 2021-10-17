(ns kosa.middleware.exception
  (:require [clojure.pprint :refer [pprint]]
            [reitit.ring.middleware.exception :as exception]
            [kuti.mailer :as mailer]
            [kosa.config :as config]))

(derive ::error ::exception)
(derive ::failure ::exception)

(defn <-pp [o]
  (with-out-str (pprint o)))

(defn handler [ex-type exception request]
  (let [body {:message (ex-message exception)
              :exception-type ex-type
              :exception (str (.getClass exception))
              ;; TODO: it would be better to return an encoded map of this data instead
              ;;       of splatting the entire thing into a string, but somewhere
              ;;       cheshire seems to fail in encoding the full ex-data map.
              :data (ex-data exception)
              :uri (:uri request)}]
    (when (-> config/config :mailer :enabled)
      (mailer/send-alert (<-pp body)))
    {:status 500
     :body (assoc body :data (str (ex-data exception)))}))

(def exception-middleware
  (exception/create-exception-middleware
    (merge
      exception/default-handlers
      {;; ex-data with :type ::error
       ::error (partial handler "error")

       ;; ex-data with ::exception or ::failure
       ::exception (partial handler "exception")

       ;; override the default handler
       ::exception/default (partial handler "default")

       ;; print stack-traces for all exceptions
       ::exception/wrap (fn [handler e request]
                          (println "ERROR" (pr-str (:uri request)))
                          (handler e request))})))
