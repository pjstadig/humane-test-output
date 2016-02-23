(ns pjstadig.util
  #?(:clj (:use [clojure.test]))
  (:require #?@(:clj  [[clojure.pprint :as pp]
                       [pjstadig.macro :as m]]
                :cljs [[cljs.pprint :as pp :include-macros true]
                       [pjstadig.macro :as m :include-macros true]
                       [cljs.test :refer [inc-report-counter! testing-vars-str testing-contexts-str get-current-env]]]))
  #?(:cljs (:import [goog.string StringBuffer])))

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
      #?(:clj (binding [*out* (pp/get-pretty-writer *out*)]
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
                    (print-expected actual))))
        :cljs (let [sb (StringBuffer.)]
                (binding [*out* (pp/get-pretty-writer (StringBufferWriter. sb))]
                  (let [print-expected (fn [actual]
                                         (-write *out* "expected: ")
                                         (pp/pprint expected *out*)
                                         (-write *out* "  actual: ")
                                         (pp/pprint actual *out*)
                                         (*print-fn* (str sb))
                                         (.clear sb)
                        )]
                    (if (seq diffs)
                      (doseq [[actual [a b]] diffs]
                        (print-expected actual)
                        (-write *out* "    diff:")
                        (if a
                          (do (-write *out* " - ")
                            (pp/pprint a *out*)
                            (-write *out* "          + "))
                          (-write *out* " + "))
                        (when b
                          (pp/pprint b *out*))
                        (*print-fn* (str sb))
                        (.clear sb))
                      (print-expected actual)))))))

(defn- convert-event [{:keys [actual expected] :as event}]
  (let [diffs (when (and (seq? actual)
                         (seq actual)
                         (= 'not (first actual))
                         (seq? (second actual))
                         (seq (second actual))
                         (#{'clojure.core/= '=} (first (second actual)))
                         (< 2 (count (second actual))))
                (let [a (nth (second actual) 1)
                      more (drop 2 (second actual))]
                  (map vector
                       more
                       (map #(take 2 (m/diff a %)) more))))
        expected (if (seq diffs)
                   (nth (second actual) 1)
                   expected)]
    (assoc event
           :diffs diffs
           :expected expected)))


(defn define-fail-report []
  #?(:clj (defmethod report :fail [event]
            (with-test-out
            (report- (convert-event event))))
    :cljs (defmethod cljs.test/report [:cljs.test/default :fail]
            [event]
            (report- (convert-event event)))))

