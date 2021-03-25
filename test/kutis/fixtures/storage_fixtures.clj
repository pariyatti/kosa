(ns kutis.fixtures.storage-fixtures
  (:require [kutis.storage :as storage]
            [mount.core :as mount]))

(defn set-service-config [t]
  (mount/stop #'storage/service-config)
  (-> (mount/with-args {:storage {:service :disk
                                  :root    "tmp/storage/"
                                  :path    "/uploads"}})
      (mount/only #{#'storage/service-config})
      mount/start)
  (t))
