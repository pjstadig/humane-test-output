(ns pjstadig.print
  (:require [cljs.pprint :as pp :include-macros true]
            [pjstadig.macro :as m])
  (:import [goog.string StringBuffer]))

(def ^:dynamic *sb*)

(defn rprint [s]
  (-write *out* s))

(defn clear []
  (*print-fn* (str *sb*))
  (.clear *sb*))

(defn with-pretty-writer [f]
  (binding [*sb* (StringBuffer.)
            *out* (pp/get-pretty-writer (StringBufferWriter. *sb*))]
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
