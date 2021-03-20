(ns kutis.fixtures.storage-fixtures
  (:require [kutis.storage :as storage]))

(defn set-service-config [t]
  (storage/set-service-config! {:service :disk
                                :root    "resources/storage/"
                                :path    "/uploads"})
  (t))
