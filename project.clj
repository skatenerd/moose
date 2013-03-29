(defproject moose "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [aleph "0.2.1-beta2"]
                 [ring "1.1.0-beta2"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.0-beta1"]
                 [org.clojure/clojurescript "0.0-1006"]]

  :ring {:handler moose.core/app}
  :main moose.core)
