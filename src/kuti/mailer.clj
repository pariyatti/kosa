(ns kuti.mailer
  (:require [postal.core :as postal]
            [kosa.config :as config]
            [clojure.tools.logging :as log]))

(defn send-mail
  "Sends a mail using the `postal` map format.

  {:from    \"no-reply@pariyatti.org\"
   :to      \"webmaster@pariyatti.org\"
   :subject \"Pariyatti Kosa: Alert\"
   :body    \"Processing of some job failed.\"}"
  [email]
  (let [mailer-config (-> config/config :mailer)
        msg (merge (:default-options mailer-config) email)]
    (if (= "localhost" (:host mailer-config))
      (postal/send-message msg)
      (postal/send-message mailer-config msg))))

(defn send-alert
  "Sends an alert, assuming :from, :to, and :subject
   are already set by config in [:mailer :default-options]."
  [body]
  (try
    (send-mail {:body body})
    (catch Throwable e
      (log/error (str "Error while sending mail: " e)))))
