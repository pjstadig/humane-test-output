(ns pjstadig.run-all
  (:require [cljs.test
             :refer [inc-report-counter! testing-vars-str testing-contexts-str get-current-env]
             :refer-macros [run-all-tests run-tests]]
            [pjstadig.humane-test-output.formatting-test]
            [pjstadig.humane-test-output.records-test]
            [clojure.data :refer [diff]]
            [cljs.pprint :as pp]
            [pjstadig.macro :refer [do-report]]
            [pjstadig.util :as util])
  (:require-macros [pjstadig.assert-expr]))

(enable-console-print!)

(def pprint-map (get-method pp/simple-dispatch :map))

(defmethod pp/simple-dispatch :map [amap]
  (if (record? amap)
    (util/pprint-record amap)
    (pprint-map amap)))

(defmethod cljs.test/report [:cljs.test/default :fail]
  [{:keys [type expected actual diffs message] :as event}]
    (inc-report-counter! :fail)
    (println "\nFAIL in" (testing-vars-str event))
    (when (:testing-contexts (get-current-env)) (println (testing-contexts-str)))
    (when message (println message))
    (binding [*out* (pp/get-pretty-writer *out*)]
      (let [print-expected (fn [actual]
                             (print "expected: ")
                             (pp/pprint expected)
                             (print "  actual: ")
                             (pp/pprint actual))]
        (if (seq diffs)
          (doseq [[actual [a b]] diffs]
            (print-expected actual)
            (print "    diff:")
            (if a
              (do (print " - ")
                (pp/pprint a)
                (print "          + "))
              (print " + "))
            (when b
              (pp/pprint b)))
          (print-expected actual)))))

(defn ^:export run []
  (run-all-tests #"pjstadig.*-test"))

