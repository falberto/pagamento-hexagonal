(defproject picpago "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [metosin/reitit "0.7.0-alpha5"]
                 [com.taoensso/timbre "6.2.2"]
                 [org.xerial/sqlite-jdbc "3.43.0.0"]
                 [io.pedestal/pedestal.service "0.6.0"]
                 [ragtime "0.8.0"]
                 [com.github.seancorfield/next.jdbc "1.3.883"]
                 [metosin/ring-swagger-ui "5.0.0-alpha.0"]]
  :plugins [[lein-ring "0.12.6"]]
  :profiles {:dev {:source-paths ["dev"]}
             :uberjar {:aot :all}}
  :repl-options {:init-ns picpago.core})
