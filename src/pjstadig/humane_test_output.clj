(ns pjstadig.humane-test-output
  (:use [clojure.data]
        [clojure.pprint :only [get-pretty-writer pprint]]
        [clojure.test]))

(defmethod assert-expr '= [msg [_ a & more]]
  `(let [a# ~a
         more# (list ~@more)
         result# (apply = a# more#)]
     (if result#
       (do-report {:type :pass, :message ~msg,
                   :expected a#, :actual more#})
       (do-report {:type :fail, :message ~msg,
                   :expected a#, :actual more#,
                   :diffs (zipmap more# (map #(take 2 (diff a# %)) more#))}))
     result#))

(defmethod clojure.test/report :fail
  [{:keys [type expected actual diffs message] :as event}]
  (with-test-out
    (inc-report-counter :fail)
    (println "\nFAIL in" (testing-vars-str event))
    (when (seq *testing-contexts*) (println (testing-contexts-str)))
    (when message (println message))
    (binding [*out* (get-pretty-writer *out*)]
      (print "expected: ")
      (pprint expected)
      (if (seq diffs)
        (doseq [[actual [a b]] diffs]
          (print "  actual: ")
          (when (or a b)
            (pprint actual)
            (print "    diff:")
            (if a
              (do (print " - ")
                  (pprint a)
                  (print "          + "))
              (print " + "))
            (when b
              (pprint b))))
        (do (print "  actual: ")
            (pprint actual))))))
