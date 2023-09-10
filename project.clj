(defproject picpago "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.taoensso/timbre "6.2.2"]
                 [com.fzakaria/slf4j-timbre "0.4.0"]
                 [ring "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [metosin/reitit "0.7.0-alpha5"]
                 [org.xerial/sqlite-jdbc "3.43.0.0"]
                 [com.github.seancorfield/next.jdbc "1.3.883"]
                 [ragtime "0.8.0"]
                 [metosin/ring-swagger-ui "5.0.0-alpha.0"]
                 [io.pedestal/pedestal.service "0.6.0"]
                 [com.brunobonacci/mulog "0.9.0"]]
  :plugins [[lein-ring "0.12.6"]]
  :profiles {:dev     {:source-paths ["dev"]}
             :uberjar {:aot :all}}
  :repl-options {:init-ns picpago.core})
