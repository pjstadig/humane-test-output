(defproject pjstadig/humane-test-output "0.11.0-SNAPSHOT"
  :description "Humane test output for clojure.test"
  :url "http://github.com/pjstadig/humane-test-output/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["releases" :clojars]]
  :test-selectors
  {:default (complement :intentionally-failing)
   :yes-i-know-the-tests-are-supposed-to-fail :intentionally-failing}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.8.0"]
                                  [org.clojure/clojurescript "1.10.516"]
                                  [org.seleniumhq.selenium/selenium-java "2.52.0"]
                                  [com.codeborne/phantomjsdriver "1.2.1"]]
                   :plugins [[lein-cljsbuild "1.1.7"]
                             [lein-doo "0.1.10"]]
                   :cljsbuild {:builds [{:id "test"
                                         :source-paths ["src" "test"]
                                         :compiler {:main pjstadig.run-all
                                                    :target :browser
                                                    :optimizations :none
                                                    :output-to "target/js/compiled/humanize-test-output-test.js"
                                                    :output-dir "target"
                                                    :source-map-timestamp true
                                                    :warnings {:private-var-access false}}}]}}
             :test {:injections [(require 'pjstadig.humane-test-output)
                                 (pjstadig.humane-test-output/activate!)]}})
