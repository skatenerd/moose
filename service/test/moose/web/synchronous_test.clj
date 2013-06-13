(ns moose.web.synchronous-test
  (:use clojure.test
        moose.web.synchronous
        moose.state)
  (:require
   [moose.config :as config])
  )

(use-fixtures :each  (fn  [f] (reset-state!) (f)))

(deftest
  reset-test
  (testing
    "nothin happens when password is wrong"
    (add-requestor "abc" "frank")
    (attempt-reset-state "wrong password")
    (is (not (nil? (owner "abc")))))
  (testing
    "works when password is right"
    (add-requestor "abc" "frank")
    (attempt-reset-state config/state-reset-password)
    (is (nil? (owner "abc")))))
