(ns moose.state-test
  (:use clojure.test
        moose.state))

(deftest
  add-requestor-test
  (testing
    "it adds a requestor"
    (let [state (ref {"abc" ["judy", "stephen"]})]
      (add-requestor "abc" "chris henderson" state)
      (is (= ["judy", "stephen", "chris henderson"] (get @state "abc"))))))
