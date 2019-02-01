(ns pjstadig.humane-test-output.records-test
  (:require [clojure.test #?(:clj :refer :cljs :refer-macros) [deftest testing is]]))

(defrecord ARecord [foo])
(defrecord BRecord [foo])

(deftest ^:intentionally-failing t-records
  (testing "THESE TESTS ARE INTENDED TO FAIL"
    (testing "these should not print as plain maps"
      (is (= (->ARecord :foo) (->ARecord :bar))))
    (testing "there should be a diff here"
      (is (= (->ARecord :foo) {:foo :foo})))
    (testing "and here"
      (is (= (->ARecord :foo) (->BRecord :foo))))))
