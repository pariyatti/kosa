(ns kuti.fixtures.storage-fixtures
  (:require [kuti.storage.core :as storage-core]
            [mount.core :as mount]))

(defn set-service-config [t]
  (mount/stop #'storage-core/service-config)
  (-> (mount/with-args {:storage {:service :disk
                                  :root    "tmp/storage/"
                                  :path    "/uploads"}})
      (mount/only #{#'storage-core/service-config})
      mount/start)
  (t))
