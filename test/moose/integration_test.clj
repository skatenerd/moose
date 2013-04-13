(ns moose.integration-test
  (:use clojure.test
        lamina.core
        moose.message
        moose.web.asynchronous)
  (:require
    [aleph.formats :as formats]))

(deftest
  does-the-dance
  (testing
    "two clients, both request, one relinquishes"
    (let [[karl-input karl-output] (channel-pair)
          [bill-input bill-output] (channel-pair)
          request-message (formats/encode-json->string
                            {:action "request" :token "abc"})
          grant-message #(formats/encode-json->string
                           (build-message-to % "grant" "abc"))]
      (async-app karl-output {})
      (async-app bill-output {})
      (enqueue karl-input "karl")
      (enqueue bill-input "bill")
      (enqueue karl-input request-message)
      (enqueue bill-input request-message)
      (let [first-karl-message (wait-for-result (read-channel karl-input) 100)]
        (is (= (grant-message "0::::karl") first-karl-message)))
      )))
