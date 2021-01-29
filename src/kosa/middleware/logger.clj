(ns kosa.middleware.logger
  (:require [ring.logger]))

(def log-request-start-middleware
  "Ring middleware to log basic information about a request.

  Adds the key :ring.logger/start-ms to the request map

  Options may include:

    * log-fn: used to do the actual logging. Accepts a map with keys
              [level throwable message]. Defaults to `clojure.tools.logging/log`.
    * transform-fn: transforms a log item before it is passed through to log-fn. Messsage types
              it might need to handle: [:starting]. It can filter messages by returning nil.
              Receives a map (a log item) with keys: [:level :throwable :message].
    * request-keys: Keys from the request that will be logged (unchanged) in addition to the data
              that ring.logger adds like [::type ::ms :status].
              Defaults to [:request-method :uri :server-name]"
  {:name ::log-request-start
   :wrap ring.logger/wrap-log-request-start})

(defn wrap-wrap-log-request-params [handler]
  (ring.logger/wrap-log-request-params handler {:transform-fn #(assoc % :level :info)}))

(def log-request-params-middleware
  "Ring middleware to log the parameters from each request

  Parameters are redacted by default, replacing the values that correspond to
  certain keys to \"[REDACTED]\". This is to prevent sensitive information from
  being written out to logs.

  Options may include:

    * log-fn: used to do the actual logging. Accepts a map with keys
              [level throwable message]. Defaults to `clojure.tools.logging/log`.
    * transform-fn: transforms a log item before it is passed through to log-fn. Messsage
              type it needs to handle: :params. It can filter messages by returning nil.
              Receives a map (a log item) with keys: [:level :throwable :message].
    * request-keys: Keys from the request that will be logged (unchanged) in addition to
              the data that ring.logger adds like [::type :params].
              Defaults to [:request-method :uri :server-name]
    * redact-key?: fn that is called on each key in the params map to check whether its
              value should be redacted. Receives the key, returns truthy/falsy. A common
              pattern is to use a set.
              Default value: #{:authorization :password :token :secret :secret-key :secret-token}"
  {:name ::log-request-params
   :wrap wrap-wrap-log-request-params})

(def log-response-middleware
  "Ring middleware to log response and timing for each request.

  Takes the starting timestamp (in msec.) from the :ring.logger/start-ms key in
  the request map, or System/currentTimeMillis if that key is not present.

  Options may include:

    * log-fn: used to do the actual logging. Accepts a map with keys
              [level throwable message]. Defaults to `clojure.tools.logging/log`.
    * transform-fn: transforms a log item before it is passed through to log-fn. Messsage types
              it might need to handle: [:finish :exception]. It can filter messages by
              returning nil. Receives a map (a log item) with keys: [:level :throwable :message].
    * request-keys: Keys from the request that will be logged (unchanged) in addition to the data
              that ring.logger adds like [::type ::ms :status].
              Defaults to [:request-method :uri :server-name]
    * log-exceptions?: When true, logs exceptions as an :error level message, rethrowing
              the original exception. Defaults to true"
  {:name ::log-response
   :wrap ring.logger/wrap-log-response})
