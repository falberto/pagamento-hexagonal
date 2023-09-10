(ns picpago.adapter.sql.sqlite
  (:require [picpago.config :as config]
            [picpago.ports.entities :as ports.entities]
            [next.jdbc :as next.jdbc]))

(defmethod ports.entities/create! :user/create-pessoa [command]
  (let [result (next.jdbc/execute! config/sqlite ["select * from pessoa"])]
    (println command " - " result)
    result))

(comment
  (ports.entities/create! {:entity/type :user/create-pessoa})
  :a)