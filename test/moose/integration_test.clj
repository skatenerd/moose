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
    (let [karl-channel (named-channel "karl")
          bill-channel (named-channel "bill")
          request-message (formats/encode-json->string
                            {:action "request" :token "abc"})]
      (async-app karl-channel {})
      (async-app bill-channel {})
      (enqueue karl-channel "karl")
      (enqueue bill-channel "bill")
      ;(prn request-message)
      ;(prn (decode-json request-message))
      ;(enqueue karl-channel request-message)
      ;(enqueue bill-channel request-message)

      )))
