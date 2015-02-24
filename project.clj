(defproject pjstadig/humane-test-output "0.7.0-SNAPSHOT"
  :description "Humane test output for clojure.test"
  :url "http://github.com/pjstadig/humane-test-output/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :test {:injections [(require 'pjstadig.humane-test-output)
                                 (pjstadig.humane-test-output/activate!)]}})
