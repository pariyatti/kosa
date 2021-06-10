(ns kuti.storage
  (:require [kuti.storage.core :as core]
            [kuti.storage.nested :as nested]))

(defn service-filename [attachment]
  (core/service-filename attachment))

(defn params->attachment! [p]
  (core/params->attachment! p))

(defn attach!
  "`attr` must be of the form `:<name>-attachment`"
  [doc attr file-params]
  (core/attach! doc attr file-params))

(defn reattach!
  "`attr` must be of the form `:<name>-attachment`
   `id` must be an existing attachment"
  [doc attr id]
  (core/reattach! doc attr id))

(defn file [attachment]
  (core/file attachment))

(defn url [attachment]
  (nested/url attachment))

(defn collapse-all [doc]
  (nested/collapse-all doc))

(defn expand-all [doc]
  (nested/expand-all doc))
