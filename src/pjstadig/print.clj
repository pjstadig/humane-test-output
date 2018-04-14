(ns pjstadig.print
  (:require [clojure.pprint :as pp]))

(def rprint print)

(defn clear [])

(defn with-pretty-writer [f]
  (binding [*out* (pp/get-pretty-writer *out*)]
    (f)))
