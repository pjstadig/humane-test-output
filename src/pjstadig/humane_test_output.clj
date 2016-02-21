(ns pjstadig.humane-test-output
  (:use [clojure.test]
        [pjstadig.util])
  (:require [clojure.data :as data]
            [clojure.pprint :as pp]))

(defn =-body
  [msg a more]
  `(let [a# ~a]
     (if-let [more# (seq (list ~@more))]
       (let [result# (apply = a# more#)]
         (if result#
           (do-report {:type :pass, :message ~msg,
                       :expected a#, :actual more#})
           (do-report {:type :fail, :message ~msg,
                       :expected a#, :actual more#,
                       :diffs (map vector
                                   more#
                                   (map #(take 2 (data/diff a# %))
                                        more#))}))
         result#)
       (throw (Exception. "= expects more than one argument")))))

(defonce activation-body
  (delay
   (when (not (System/getenv "INHUMANE_TEST_OUTPUT"))
     (defmethod assert-expr '= [msg [_ a & more]]
       (=-body msg a more))
     (defmethod assert-expr 'clojure.core/= [msg [_ a & more]]
       (=-body msg a more))

     (defmethod report :fail
       [{:keys [type expected actual diffs message] :as event}]
       (with-test-out
         (inc-report-counter :fail)
         (println "\nFAIL in" (testing-vars-str event))
         (when (seq *testing-contexts*) (println (testing-contexts-str)))
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
               (print-expected actual))))))
     ;; this code is just yanked from clojure.pprint
     (defmethod pp/simple-dispatch clojure.lang.IRecord [arec]
       (pprint-record arec))
     (prefer-method pp/simple-dispatch
                    clojure.lang.IRecord
                    clojure.lang.IPersistentMap))))

(defn activate! []
  @activation-body)
