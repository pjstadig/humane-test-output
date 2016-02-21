(ns pjstadig.macro
  (:require [cljs.test]
            [clojure.data :as data :include-macros true]))

(defn diff [& args]
  (apply data/diff args))

(defn do-report [& args]
  (apply cljs.test/do-report args))


