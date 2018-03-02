(ns pjstadig.macro
  (:require #?(:cljs [cljs.test])
            [clojure.data :as data]))

(defn diff [& args]
  (apply data/diff args))

(defn do-report [& args]
  #?(:cljs (apply cljs.test/do-report args)))


