(ns kosa.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [kosa.cli :as cli]
            [kosa.config :as config]
            [kosa.server :as server]
            [kutis.record :as db]
            [mount-up.core :as mu]
            [mount.core :as mount]))

(defn- log-mount-action [action-map]
  (fn [{:keys [name action]}]
    (log/info {:event (action-map action)
               :state name})))

(defn mount-init! []
  (mu/all-clear)
  (mu/on-upndown :before-info
                 (log-mount-action {:up ::state-up-pre
                                    :down ::state-down-pre})
                 :before)
  (mu/on-upndown :after-info
                 (log-mount-action {:up ::state-up-post
                                    :down ::state-down-post})
                 :after)
  (mu/on-up :around-exceptions
            (mu/try-catch
             (fn [ex {:keys [name]}] (log/error ex {:event     ::state-up-failure
                                                    :state     name
                                                    :exception ex})))
            :wrap-in))

(defn start [opts]
  (log/info (format "Starting the server with options: %s" opts))
  (-> (mount/with-args opts)
      (mount/only #{#'config/config
                    #'db/crux-node
                    #'server/server})
      mount/start)
  (log/info "Kosa started."))

(defn stop []
  (mount/stop)
  (log/info "Kosa stopped."))

(defn load-config! [config-file]
  (mount/stop #'config/config)
  (-> (mount/with-args {:options {:config-file config-file}})
      (mount/only #{#'config/config})
      mount/start))

(defn migrate [opts]
  ;; (do (mount/start-with-args opts #'config/config)
  ;;     (migrations/migrate))
  (throw (ex-info "Migrations are not implemented yet." {})))

(defn rollback [opts]
  ;; (do (mount/start-with-args opts #'config/config)
  ;;     (migrations/rollback))
  (throw (ex-info "Migrations are not implemented yet." {})))

(defn print-help [opts]
  (println (cli/help-message opts)))

(defn -main
  [& args]
  (mount-init!)
  (let [opts (cli/parse args)
        _ (println "opts have been parsed")]
    (if-let [opts-error (cli/error-message opts)]
      (do
        (println opts-error)
        (System/exit 1))
      (case (cli/operational-mode opts)
        :help (print-help opts)
        :start (start opts)
        :migrate (migrate opts)
        :rollback (rollback opts)
        (print-help opts)))))
