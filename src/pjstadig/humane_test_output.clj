(ns pjstadig.humane-test-output
  (:use [clojure.test]
        [pjstadig.util])
  (:require [clojure.data :as data]
            [clojure.pprint :as pp]))

(defonce activation-body
  (delay
   (when (not (System/getenv "INHUMANE_TEST_OUTPUT"))
     (define-fail-report)
     ;; this code is just yanked from clojure.pprint
     (defmethod pp/simple-dispatch clojure.lang.IRecord [arec]
       (pprint-record arec))
     (prefer-method pp/simple-dispatch
                    clojure.lang.IRecord
                    clojure.lang.IPersistentMap))))

(defn activate! []
  @activation-body)
