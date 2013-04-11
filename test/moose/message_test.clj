(ns moose.message-test
  (:use clojure.test
        moose.message)
  (:require
    [clojure.string :as string]
    ))

(deftest
  decode-json-test
  (testing
    "it decodes valid message"
    (let [message (string/escape
                    "{'action':'request','token':'abc'}"
                    {\' "\""})]
    (is (=
          {:action "request", :token "abc"}
          (decode-json message))))))

(deftest
  for-recipient-test
  (testing
    "knows when its for a given recipient"
    (is ((for-user? "judy") {:recipient "judy"}))
    (is (not ((for-user? "stephen") {:recipient "judy"})))))

(deftest
  for-token-test
  (testing
    "knows when its for a given token"
    (is ((for-token? "882828") {:token "882828"}))
    (is (not ((for-token? "something else") {:token "882828"})))))

(deftest
  build-message-to-test
  (testing
    "builds for a recipient"
    (let [built-message (build-message-to "judy" :grant "h4873295")]
      (is (= built-message
             {:recipient "judy"
              :event :grant
              :token "h4873295"})))))

(deftest
  build-message-from-test
  (testing
    "builds from a sender"
    (let [built-message (build-message-from "judy" :grant "h4873295")]
      (is (= built-message
             {:sender "judy"
              :event :grant
              :token "h4873295"})))))
