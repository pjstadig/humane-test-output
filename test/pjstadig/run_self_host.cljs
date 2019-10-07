(ns pjstadig.run-self-host
  (:require [cljs.test :refer-macros [run-all-tests]]
            [pjstadig.humane-test-output]
            [pjstadig.humane-test-output.formatting-test]
            [pjstadig.humane-test-output.records-test]
            [pjstadig.humane-test-output.reporting-test]))

(enable-console-print!)

(defn ^:export run []
  (run-all-tests #"pjstadig.*-test"))
