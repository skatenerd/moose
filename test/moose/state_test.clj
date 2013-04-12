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

(deftest
  remove-requestor-test
  (testing
    "removes the first requestor"
    (let [state (ref {"abc" ["judy", "stephen"]})]
      (remove-requestor "abc" "judy" state)
      (is (= ["stephen"] (get @state "abc")))))
  (testing
    "removes all instances of requestor"
    (let [state (ref {"abc" ["ralph" "judy" "ralph" "stephen" "ralph"]})]
      (remove-requestor "abc" "ralph" state)
      (is (= ["judy" "stephen"] (get @state "abc")))))
  (testing
    "returns new holder"
    (let [state (ref {"abc" ["judy", "stephen"]})]
      (is (= "stephen" (remove-requestor "abc" "judy" state)))))
  (testing
    "returns nil of no new holder"
    (let [state (ref {"abc" ["judy"]})]
      (is (nil? (remove-requestor "abc" "not there" state)))
      (is (nil? (remove-requestor "abc" "judy" state))))
    )
  )
