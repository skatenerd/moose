(ns moose.state-test
  (:use clojure.test
        moose.state))

(deftest
  add-requestor-test
  (testing
    "it adds a requestor"
    (let [state (ref {"abc" ["judy", "stephen"]})]
      (add-requestor "abc" "chris henderson" state)
      (is (= ["judy", "stephen", "chris henderson"] (get @state "abc")))))
  (testing
    "it does not add requestor who is already there"
    (let [state (ref {"abc" ["judy", "stephen"]})]
      (add-requestor "abc" "judy" state)
      (add-requestor "abc" "stephen" state)
      (is (= ["judy", "stephen"] (get @state "abc")))))

  (testing
    "it returns the number of people (including owner) who have to relinquish before you"
    (let [state (ref {"abc" ["judy", "stephen"]})
          response (add-requestor "abc" "music snob" state)]
      (is (= "judy" (:holder response)))))

  (testing
      "it returns the number of people (including owner) who have to relinquish before you"
      (let [state (ref {"abc" ["judy", "stephen"]})
            response (add-requestor "abc" "music snob" state)]
        (is (= 2 (:queue-length response)))))

  )

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
