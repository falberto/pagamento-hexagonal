(ns picpago.ports.entities
  (:require [picpago.domain.models :as domain.models]))

(defn- -create! [command]
  (domain.models/check-entity-spec! (:entity/type command)
                                    (:command/data command))
  (:entity/type command))

(defn- -update! [command]
  (domain.models/check-entity-spec! (:entity/type command)
                                    (:command/data command))
  (:entity/type command))

(defn- -delete! [command]
  (:entity/type command))

(defn- -select [entity-type query-params]
  (tap> {:fn           ::select
         :entity-type  entity-type
         :query-params query-params})
  entity-type)

(defmulti create! #'-create!)
(defmulti update! #'-update!)
(defmulti delete! #'-delete!)
(defmulti select #'-select)
