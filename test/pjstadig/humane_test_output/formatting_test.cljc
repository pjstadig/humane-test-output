(ns pjstadig.humane-test-output.formatting-test
  #?(:clj  (:use [clojure.test]
                 [pjstadig.macro])
     :cljs (:require-macros [cljs.test :refer [deftest testing is]]
                            [pjstadig.macro :refer [deftest+]])))

(deftest t-formatting
  (testing "FIXME, I fail."
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

(deftest+ t-macro-wrapping 1 2)
