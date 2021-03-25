(ns kosa.config
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [aero.core :as aero]
            [mount.core :as mount :refer [defstate]]
            [clojure.tools.logging :as log]))

(s/def :db-spec/data-dir string?)

(s/def ::db-spec
  (s/keys :req-un
          [:db-spec/data-dir]))

(s/def ::port int?)
(s/def ::supported-languages (s/coll-of string? :kind vector?))

(s/def ::config (s/keys :req-un [::db-spec ::port ::supported-languages]))

(defn start-config! []
  (let [config-file (get-in (mount/args) [:options :config-file])
        _ (log/info (format "Reading config from '%s'." config-file))
        config-read (aero/read-config config-file)]
    (if (s/valid? ::config config-read)
      config-read
      (throw (ex-info "Failed to validate config" {})))))

(defstate config
  :start (start-config!)
  :stop nil)

(defn supported-languages []
  (-> config (get :supported-languages) set))
