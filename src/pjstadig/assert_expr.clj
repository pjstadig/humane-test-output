(ns pjstadig.assert-expr
  (:require [cljs.test :refer [assert-expr]]))

(defn =-body
  [msg a more]
  `(let [a# ~a]
     (if-let [more# (seq (list ~@more))]
       (let [result# (apply = a# more#)]
         (if result#
           (~'cljs.test/do-report {:type :pass, :message ~msg,
                       :expected a#, :actual more#})
           (~'cljs.test/do-report {:type :fail, :message ~msg,
                       :expected a#, :actual more#,
                       :diffs (map vector
                                   more#
                                   (map #(take 2 (clojure.data/diff a# %))
                                        more#))}))
         result#)
       (throw (Exception. "= expects more than one argument")))))

(defmethod assert-expr '= [menv msg [_ a & more]]
  (=-body msg a more))

(defmethod assert-expr 'cljs.core/= [menv msg [_ a & more]]
  (=-body msg a more))

