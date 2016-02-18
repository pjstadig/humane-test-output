(ns pjstadig.humane-test-output.formatting-test
  (:use [clojure.test]))

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

(defmacro deftest+
  [test-name expected actual]
  `(deftest ~test-name
     (is (= ~expected ~actual) "THIS ONE SHOULD ALSO FAIL")))

(deftest+ ^:intentionally-failing t-macro-wrapping 1 2)
