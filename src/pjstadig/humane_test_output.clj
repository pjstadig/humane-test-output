(ns pjstadig.humane-test-output
  (:use [clojure.data]
        [clojure.pprint :only [get-pretty-writer pprint]]
        [clojure.test]))

(defmethod assert-expr '= [msg [_ a & more]]
  `(let [a# ~a]
     (if-let [more# (seq (list ~@more))]
       (let [result# (apply = a# more#)]
         (if result#
           (do-report {:type :pass, :message ~msg,
                       :expected a#, :actual more#})
           (do-report {:type :fail, :message ~msg,
                       :expected a#, :actual more#,
                       :diffs (map vector more# (map #(take 2 (diff a# %)) more#))}))
         result#)
       (throw (Exception. "= expects more than one argument")))))

(defmethod clojure.test/report :fail
  [{:keys [type expected actual diffs message] :as event}]
  (with-test-out
    (inc-report-counter :fail)
    (println "\nFAIL in" (testing-vars-str event))
    (when (seq *testing-contexts*) (println (testing-contexts-str)))
    (when message (println message))
    (binding [*out* (get-pretty-writer *out*)]
      (let [print-expected (fn [actual]
                             (print "expected: ")
                             (pprint expected)
                             (print "  actual: ")
                             (pprint actual))]
        (if (seq diffs)
          (doseq [[actual [a b]] diffs
                  :when (or a b)]
            (print-expected actual)
            (print "    diff:")
            (if a
              (do (print " - ")
                  (pprint a)
                  (print "          + "))
              (print " + "))
            (when b
              (pprint b)))
          (print-expected actual))))))
