(ns picpago.domain.core
  (:require [picpago.ports.entities :as ports.entities]
            [picpago.domain.models :as domain.models]))

(defn- -create! [command]
  (domain.models/check-entity-spec! (:entity/type command)
                                    (:command/data command))
  (:entity/type command))


(def create-entity!
  (fn [{:keys [tx-data] :as context}]
    (let [id (ports.entities/create! tx-data)]
      (assoc-in context [:tx-data :entity/id] id))))

(defmulti create! #'-create!)

;; Entities
(defn available-entities []
  (keys (methods ports.entities/select)))

