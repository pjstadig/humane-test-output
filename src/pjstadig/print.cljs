(ns pjstadig.print
  (:require [cljs.pprint :as pp :include-macros true]
            [pjstadig.macro :as m])
  (:import [goog.string StringBuffer]))

;; fix #37 - https://github.com/pjstadig/humane-test-output/issues/37
(defonce ^:private sb (StringBuffer.))

(defn rprint [s]
  (cljs.core/-write *out* s))

(defn clear []
  (*print-fn* (.toString sb))
  (.clear sb))

(defn with-pretty-writer [f]
  (binding [*out* (pp/get-pretty-writer (StringBufferWriter. sb))]
    (f)))

(defn convert-event [{:keys [actual expected] :as event}]
  (let [diffs (when (and (seq? actual)
                         (seq actual)
                         (= 'not (first actual))
                         (seq? (second actual))
                         (seq (second actual))
                         (#{'clojure.core/= '= 'cljs.core/=} (first (second actual)))
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
