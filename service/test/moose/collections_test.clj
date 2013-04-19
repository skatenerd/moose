(ns moose.collections-test
  (:use clojure.test
        moose.collections))

(deftest
  iterative-contains-test
  (testing
    "true"
    (is (iterative-contains? 2 [1 2 3])))
  (testing
    "false"
    (is (not (iterative-contains? 9 [1 2 3])))))

(deftest
  conjv-test
  (testing
    "puts new element on the end of a vector"
    (is (= 9 (last (conjv [1 2 3] 9))))))
