(ns kuti.storage.nested
  (:require [clojure.string :as str]
            [kuti.support :refer [path-join]]
            [kuti.record.nested :as record]
            [kuti.storage.core :as core]
            [kuti.support.debugging :refer :all]))

(defn collapse-all [doc]
  (record/collapse-all doc "attachment"))

(defn url [attachment]
  (assert (not (nil? (:path core/service-config)))
          "Service config cannot be nil. Did you forget a test fixture?")
  (path-join (:path core/service-config)
             (core/attached-filename attachment)))

(defn expand-one [e attr-id]
  (let [expanded (record/expand-all e attr-id)
        attr (record/id->field attr-id)]
    (assoc-in expanded
              [attr :attm/url]
              (url (get expanded attr)))))

(defn expand-all [doc]
  (record/do-to-all expand-one doc "attachment-id"))
