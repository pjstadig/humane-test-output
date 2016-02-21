(ns pjstadig.run-all
  (:require [cljs.test :refer-macros [run-all-tests run-tests]]
            [pjstadig.humane-test-output.formatting-test]
            [pjstadig.humane-test-output.records-test]
            [cljs.pprint :as pp]
            [pjstadig.macro :refer [do-report]]
            [pjstadig.util :as util])
  (:require-macros [pjstadig.assert-expr]))

(enable-console-print!)

(def pprint-map (get-method pp/simple-dispatch :map))

(defmethod pp/simple-dispatch :map [amap]
  (if (record? amap)
    (util/pprint-record amap)
    (pprint-map amap)))

(util/define-fail-report)

(defn ^:export run []
  (run-all-tests #"pjstadig.*-test"))

