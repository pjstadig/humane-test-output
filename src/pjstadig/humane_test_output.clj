(ns pjstadig.humane-test-output
  (:use [clojure.data]
        [clojure.pprint :only [get-pretty-writer pprint pprint-indent pprint-logical-block write]]
        [clojure.test]))

(defmethod assert-expr '= [msg [_ a & more]]
  `(let [a# ~a
         more# (list ~@more)
         result# (apply = a# more#)]
     (if result#
       (do-report {:type :pass, :message ~msg,
                   :expected a#, :actual more#})
       (do-report (merge {:type :fail, :message ~msg,
                          :expected a#, :actual more#}
                         (if (= (count more#) 1)
                           {:actual (first more#)
                            :diff (take 2 (diff a# (first more#)))}
                           {:actual more#}))))
     result#))

(defmethod clojure.test/report :fail
  [{:keys [type expected actual diff message] :as event}]
  (with-test-out
    (inc-report-counter :fail)
    (println "\nFAIL in" (testing-vars-str event))
    (when (seq *testing-contexts*) (println (testing-contexts-str)))
    (when message (println message))
    (println "expected:")
    (pprint expected)
    (println "\nactual:")
    (pprint actual)
    (when-let [[a b] diff]
      (println "\ndiff:")
      (pprint a)
      (pprint b))))
