(ns pjstadig.assert-expr
  #?(:clj (:require [cljs.test :refer [assert-expr]]
                    [pjstadig.macro :as m])))

(defn =-body
  [msg a more]
  `(let [a# ~a
         more# (seq (list ~@more))
         result# (apply = a# more#)]
     (if result#
       (m/do-report {:type :pass, :message ~msg,
                     :expected a#, :actual more#})
       (m/do-report {:type :fail, :message ~msg,
                     :expected a#, :actual more#,
                     :diffs (map vector
                                 more#
                                 (map #(take 2 (m/diff a# %))
                                      more#))}))
     result#))

#?(:clj (defmethod assert-expr '= [menv msg [_ a & more]]
          (=-body msg a more)))

#?(:clj (defmethod assert-expr 'clojure.core/= [menv msg [_ a & more]]
          (=-body msg a more)))

