(ns moose.state-test
  (:use
    clojure.test
    moose.state)
  (:require
    [moose.config :as config]))

(use-fixtures :each  (fn  [f] (reset-state!) (f)))

(defmacro testing-both-implementations [& body]
  `(let [original-impelmentation# @config/state-implementation]
     (swap! config/state-implementation (fn [existing#] :in-memory))
     (reset-state!)
     (testing "MEMORY"
       (testing
        ~@body))
     (swap! config/state-implementation (fn [existing#] :redis))
     (reset-state!)
     (testing "REDIS"
       (testing
       ~@body))
     (swap! config/state-implementation (fn [existing#] original-impelmentation#))
     ))

(deftest
  add-requestor-test_0
  (testing-both-implementations
    "it adds a requestor"
    (add-requestor "abc" "owner")
    (add-requestor "abc" "waiter")
    (is (= ["waiter"] (waiters-for "abc")))))

(deftest
  add-requestor-test_1
  (testing-both-implementations
    "it does not add requestor who is already there"
    (add-requestor "abc" "owner")
    (add-requestor "abc" "waiter")
    (add-requestor "abc" "waiter")
    (is (= ["waiter"] (waiters-for "abc")))))

(deftest
  add-requestor-test_2
  (testing-both-implementations
    "it returns current owner of token"
    (add-requestor "abc" "owner")
    (let [response (add-requestor "abc" "waiter")]
      (is (= "owner" (:holder response)))))
  )

(deftest
  add-requestor-test_3
  (testing-both-implementations
    "it returns the number of people (including owner) who have to relinquish before you"
    (add-requestor "abc" "owner")
    (add-requestor "abc" "first-waiter")
    (let [response (add-requestor "abc" "second-waiter")]
      (is (= 2 (:queue-length response)))))
  )



(deftest
  remove-requestor-test_0
  (testing-both-implementations
    "removes the owner"
    (add-requestor "abc" "owner")
    (add-requestor "abc" "waiter")
    (remove-requestor "abc" "owner")
    (is (= "waiter" (owner "abc")))
    (is (= [] (waiters-for "abc")))))


(deftest
  remove-requestor-test_1
  (testing-both-implementations
    "returns new holder"
    (add-requestor "abc" "owner")
    (add-requestor "abc" "waiter")
    (is (= "waiter" (remove-requestor "abc" "owner" ))))
  )

(deftest
  remove-requestor-test_2
  (testing-both-implementations
    "returns nil if no new holder"
    (is (nil? (remove-requestor "abc" "not there")))
    (add-requestor "abc" "owner")
    (is (nil? (remove-requestor "abc" "still not there")))))


