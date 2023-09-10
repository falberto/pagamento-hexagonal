(ns picpago.adapter.sql.sqlite
  (:require [picpago.ports.entities :as ports.entities]))

(defmethod ports.entities/create! :user/create-pessoa [command]
  (println command))

(comment
  (ports.entities/create! {:entity/type :user/create-pessoa})
  :a)