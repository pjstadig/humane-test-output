(ns pjstadig.humane-test-output
  (:require [cljs.test :refer-macros [run-all-tests run-tests]]
            [cljs.pprint :as pp]
            [pjstadig.macro :refer [do-report]]
            [pjstadig.util :as util])
  (:require-macros [pjstadig.assert-expr]))

(def pprint-map (get-method pp/simple-dispatch :map))

(defmethod pp/simple-dispatch :map [amap]
  (if (record? amap)
    (util/pprint-record amap)
    (pprint-map amap)))

(util/define-fail-report)

