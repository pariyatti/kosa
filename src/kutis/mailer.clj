(ns kutis.mailer
  (:require [postal.core :as postal]
            [kosa.config :as config]))

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
  (send-mail {:body body}))
