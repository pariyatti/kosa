(ns joplin.migrators.crux.20210317020346-add-image-artefacts
  (:require [crux.api :as x]
            [joplin.crux.database :as d]
            [tick.alpha.api :as t]))

;; NOTE: this file is really more of a sample and should be moved into
;;       the joplin.crux tests -sd

(defn mk-tx-fn [node]
  (x/submit-tx
   node
   [[:crux.tx/put {:crux.db/id :add-owner
	                 :crux.db/fn '(fn [ctx]
	                                (let [db (crux.api/db ctx)
                                        ids (crux.api/q db
                                                        '{:find  [e]
                                                          :where [[e :type "image_artefact"]]})
                                        entities (map #(crux.api/entity db (first %)) ids)]
                                    (vec (map (fn [entity]
                                                [:crux.tx/put (assoc entity :owner nil)])
                                              entities))
	                                  ))}]]))

(defn run-tx-fn [node]
  (x/submit-tx
   node
   [[:crux.tx/fn
	   :add-owner]]))

(defn up [db]
  (let [node (d/get-node (:conf db))]
    (mk-tx-fn node)
    (run-tx-fn node))
  ;; (let [node (d/get-node (:conf db))
  ;;       txs [[:crux.tx/put {:crux.db/id id
  ;;                           :schema/id id
  ;;                           :schema/created-at (t/now)}]]]
  ;;   (d/transact! node txs (format "Migrator '%s' failed to apply." id)))
  )

;; TODO: close node when finished

(defn down [db]
  ;; (let [node (d/get-node (:conf db))
  ;;       txs [[:crux.tx/delete id]]]
  ;;   (d/transact! node txs (format "Rollback '%s' failed to apply." id)))
  )
