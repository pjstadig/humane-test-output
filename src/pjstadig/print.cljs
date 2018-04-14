(ns pjstadig.print
  (:require [cljs.pprint :as pp :include-macros true])
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
