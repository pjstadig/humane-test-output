(ns pjstadig.humane-test-output.reporting-test
  (:require [cljs.test #?(:clj :refer :cljs :refer-macros) [deftest]]
            #?@(:cljs [[pjstadig.util]
                       [pjstadig.print :as p]])))

#?(:cljs
   (def report #'pjstadig.util/report-))

#?(:cljs
   (deftest cljs-smoke-test
     (report (p/convert-event {:type :fail
                               :expected '(= {:map "srt"} {:map 1})
                               :actual '(not (= {:map "srt"} {:map 1}))
                               :message "this is a smoke test"}))))
