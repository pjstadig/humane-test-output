(ns pjstadig.util
  (:require #?(:clj  [clojure.pprint :as pp]
               :cljs [cljs.pprint :as pp :include-macros true])))

(defn- print-seq [aseq]
  (pp/pprint-logical-block
    (pp/write-out (ffirst aseq))
    (print " ")
    (pp/pprint-newline :linear)
    ;; [pjs] this is kind of ugly, but it is a private var :(
    ;; always print both parts of the [k v] pair
    #?(:clj  (.set #'pp/*current-length* 0)
       :cljs (set! pp/*current-length* 0))
    (pp/write-out (fnext (first aseq)))))


(defn pprint-record [arec]
  (pp/pprint-logical-block
    #?@(:clj  [:prefix (str "#" (.getName (class arec)) "{") :suffix "}"]
        :cljs [:prefix (re-find #".*?\{" (with-out-str (print arec))) :suffix "}"])
    (pp/print-length-loop
      [aseq (seq arec)]
      (when aseq
        (print-seq aseq)
        (when (next aseq)
          (print ", ")
          (pp/pprint-newline :linear)
          (recur (next aseq)))))))

