(ns user
  (:require [picpago.adapter.web :as core]
            [ring.adapter.jetty :as jetty])
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

(comment
  (reset!)
  :end)

