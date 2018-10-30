(ns pjstadig.util
  #?(:clj (:use [clojure.test]))
  (:require
   #?@(:clj  [[clojure.pprint :as pp]
              [pjstadig.print :as p]
              [lambdaisland.deep-diff :as deep]]
       :cljs [[cljs.pprint :as pp :include-macros true]
              [pjstadig.print :as p]
              [cljs.test :refer [inc-report-counter! testing-vars-str testing-contexts-str get-current-env]]]))
  #?(:cljs (:import [goog.string StringBuffer])))

(defn- print-seq [aseq]
  (pp/pprint-logical-block
   (pp/write-out (ffirst aseq))
   (p/rprint " ")
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
  (p/with-pretty-writer (fn []
                          (let [print-expected (fn [actual]
                                                 (p/rprint "expected: ")
                                                 (deep/pretty-print expected)
                                                 (p/rprint "  actual: ")
                                                 (deep/pretty-print actual)
                                                 (p/clear))]
                            (if (seq diffs)
                              (doseq [[actual diff] diffs]
                                (print-expected actual)
                                (p/rprint "    diff:")
                                (p/rprint " ")
                                (deep/pretty-print diff)
                                (p/clear))
                              (print-expected actual))))))

(defn define-fail-report []
  #?(:clj (defmethod report :fail [& args]
            (with-test-out
            (report- (first args))))
    :cljs (defmethod cljs.test/report [:cljs.test/default :fail]
            [event]
            (report- (p/convert-event event)))))
