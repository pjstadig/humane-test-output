(ns pjstadig.humane-test-output.formatting-test
  #?(:clj  (:use [clojure.test]
                 [pjstadig.fixtures.macro])
     :cljs (:require-macros [cljs.test :refer [deftest testing is]]
                            [pjstadig.fixtures.macro :refer [deftest+]])))

(deftest t-nothing-to-see-here
  (is true "everything should be A-OK"))

(deftest ^:intentionally-failing t-formatting
  (testing "THESE TESTS ARE INTENDED TO FAIL"
    (is (= {:foo :bar :baz :quux :something "a long string?"
            :another-key "and another value"}
           {:fo :bar}))
    (is (= {:foo :bar :baz :quux :something "a long string?"
            :another-key "and another value"}
           {:foo :bar}))
    (is (= {:foo :bar}
           {:foo :bar :baz :quux :something "a long string?"
            :another-key "and another value"}))
    (is (= {:foo :bar :baz :quux} {:foo :bar :baz :quux} {:fo :bar :baz :quux}
           {:fo :bar :baz :quux}))
    (let [foo {:foo :bar :baz :quux :something "a long string?"
               :another-key "and another value"}]
      (is (list? foo)))))

(deftest+ ^:intentionally-failing t-macro-wrapping 1 2)
