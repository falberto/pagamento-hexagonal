(ns user
  (:require [picpago.adapter.web :as core]
            [ring.adapter.jetty :as jetty]
            [ragtime.jdbc :as jdbc]
            [ragtime.core :as ragtime.core]
            [ragtime.repl :as repl]
            [picpago.config :as config])
  (:import (org.eclipse.jetty.server Server)))

(defonce server (atom nil))

(defn start! []
  (swap! server (fn [_] (jetty/run-jetty
                          #'core/app {:port  3000
                                      :join? false}))))

(defn stop! []
  (when-let [s @server]
    (.stop ^Server s)))

(defn reset! []
  (stop!)
  (start!))

(def config
  {:datastore  (jdbc/sql-database config/sqlite)
   :migrations (jdbc/load-resources "migrations")})

(comment
  (repl/migrate config)
  (reset!)
  :end)

