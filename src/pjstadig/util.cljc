(ns pjstadig.util
  #?(:clj (:use [clojure.test]))
  (:require #?@(:clj  [[clojure.pprint :as pp]]
                :cljs [[cljs.pprint :as pp :include-macros true]
                       [cljs.test :refer [inc-report-counter! testing-vars-str testing-contexts-str get-current-env]]])))

(defn- print-seq [aseq]
  (pp/pprint-logical-block
    (pp/write-out (ffirst aseq))
    #?(:clj (print " ")
       :cljs (-write *out* " "))
    (pp/pprint-newline :linear)
    ;; [pjs] this is kind of ugly, but it is a private var :(
    ;; always print both parts of the [k v] pair
    #?(:clj  (.set #'pp/*current-length* 0)
       :cljs (set! pp/*current-length* 0))
    (pp/write-out (fnext (first aseq)))))


(defn pprint-record [arec]
  (pp/pprint-logical-block
    #?@(:clj  [:prefix (str "#" (.getName (class arec)) "{") :suffix "}"]
        :cljs [:prefix (re-find #".*?\{" (with-out-str (print arec))) :suffix "}"])
    (pp/print-length-loop
      [aseq (seq arec)]
      (when aseq
        (print-seq aseq)
        (when (next aseq)
          (print ", ")
          (pp/pprint-newline :linear)
          (recur (next aseq)))))))

(defn- report-
    [{:keys [type expected actual diffs message] :as event}]
      #?(:clj  (inc-report-counter :fail)
         :cljs (inc-report-counter! :fail))
      (println "\nFAIL in" (testing-vars-str event))
      (when #?(:clj  (seq *testing-contexts*)
               :cljs (:testing-contexts (get-current-env)))
            (println (testing-contexts-str)))
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

(defn define-fail-report []
  #?(:clj (defmethod report :fail [& args]
            (with-test-out
            (apply report- args)))
    :cljs (defmethod cljs.test/report [:cljs.test/default :fail]
            [& args]
            (apply report- args))))

