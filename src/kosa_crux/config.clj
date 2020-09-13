(ns kosa-crux.config
  (:require [clojure.java.io :as io]
            [aero.core :as aero]
            [mount.core :refer [defstate]]))

(defstate config
  :start (-> "config.edn" io/resource aero/read-config))
