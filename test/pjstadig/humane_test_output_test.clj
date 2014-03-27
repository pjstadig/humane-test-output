(ns pjstadig.humane-test-output-test
  (:use [clojure.test]
        [pjstadig.humane-test-output]))

(activate!)

(deftest a-test
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
