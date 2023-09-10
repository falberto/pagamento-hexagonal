(ns picpago.adapter.sql.sqlite
  (:require [picpago.domain.models :as domain.models]
            [picpago.ports.entities :as ports.entities]))


(defmulti ports.entities/create! :user/criar-pessoa [command])
