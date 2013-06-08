(defproject
  moose
  "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.taoensso/carmine "1.8.0" :exclusions  [commons-codec]]
                 [compojure "1.1.5" :exclusions  [org.clojure/tools.macro]]
                 [aleph "0.3.0-beta15" :exclusions [
                   [lamina] org.clojure/tools.logging]]
                 [lamina "0.5.0-beta15"]
                 [hiccup "1.0.3"]]

  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}

  :ring {:handler moose.core/app}
  :main moose.web)
