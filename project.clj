(defproject picpago "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [metosin/reitit "0.7.0-alpha1"]
                 [metosin/ring-swagger-ui "5.0.0-alpha.0"]]
  :plugins [[lein-ring "0.12.6"]]
  :profiles {:dev {:source-paths ["dev"]}
             :uberjar {:aot :all}}
  :repl-options {:init-ns picpago.core})
