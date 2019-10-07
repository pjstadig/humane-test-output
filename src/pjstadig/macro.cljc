(ns pjstadig.macro
  (:require [clojure.data :as data]))

(defn diff [& args]
  (apply data/diff args))
