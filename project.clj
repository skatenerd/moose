(defproject
  moose
  "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring "1.1.0-beta2"]
                 [compojure "1.1.5"]
                 ;[lamina "0.5.0-beta13"]
                 [aleph "0.2.1-beta2"]
                 [hiccup "1.0.0-beta1"]
                 [org.clojure/clojurescript "0.0-1006"]]


  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}

  :ring {:handler moose.core/app}
  :main moose.web)
