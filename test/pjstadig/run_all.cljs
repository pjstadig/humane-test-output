(ns pjstadig.run-all
  (:require [cljs.test
             :refer [inc-report-counter! testing-vars-str testing-contexts-str get-current-env]
             :refer-macros [run-all-tests run-tests]]
            [pjstadig.humane-test-output.formatting-test]
            [pjstadig.humane-test-output.records-test]
            [cljs.pprint :as pp])
  (:require-macros [pjstadig.assert-expr]))

(enable-console-print!)

(def pprint-map (get-method pp/simple-dispatch :map))

(defn pprint-record [arec]
  (pp/pprint-logical-block
    :prefix (str "#" (with-out-str (print arec)) "{") :suffix "}"
    (pp/print-length-loop
      [aseq (seq arec)]
      (when aseq
        (pp/pprint-logical-block
          (pp/write-out (ffirst aseq))
          (print " ")
          (pp/pprint-newline :linear)
          ;; [pjs] this is kind of ugly, but it is a private var :(
          (set! pp/*current-length* 0) ; always print both parts of the [k v] pair
          (pp/write-out (fnext (first aseq))))
        (when (next aseq)
          (print ", ")
          (pp/pprint-newline :linear)
          (recur (next aseq)))))))

(defmethod pp/simple-dispatch :map [amap]
  (if (record? amap)
    (pprint-record amap)
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

