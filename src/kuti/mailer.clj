(ns kuti.mailer
  (:require [sendgrid.core :as sg]
            [kosa.config :as config]
            [clojure.tools.logging :as log]))

(defn add-trailing-newlines [s]
  (str s "\n\n---"))

(defn send-mail
  "Sends a mail using the `camdez/sendgrid` map format.

  {:from    \"no-reply@pariyatti.org\"
   :to      \"webmaster@pariyatti.org\"
   :subject \"Pariyatti Kosa: Alert\"
   :text    \"Processing of some job failed.\"}"
  [email]
  (let [mailer-config (-> config/config :mailer)
        api-key {:api-key (:sendgrid-api-key mailer-config)}
        msg (merge (:default-options mailer-config)
                   (update-in email [:text] add-trailing-newlines))]
    (sg/send-email api-key msg)))

(defn send-alert
  "Sends an alert, assuming :from, :to, and :subject
   are already set by config in [:mailer :default-options]."
  [text]
  (try
    (send-mail {:text text})
    (catch Throwable e
      (log/error (str "Error while sending mail: " e)))))
