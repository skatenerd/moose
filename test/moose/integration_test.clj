(ns moose.integration-test
  (:use clojure.test
        lamina.core
        lamina.viz
        moose.message
        moose.web.asynchronous)
  (:require
    [aleph.formats :as formats]))

(defmacro with-next-item [channel binding-name & body]
  `(let [~binding-name (wait-for-result (read-channel ~channel) 100)]
     ~@body))

(deftest
  does-the-dance
  (testing
    "two clients, both request, one relinquishes"
    (let [[karl-channel karl-handle] (channel-pair)
          [bill-channel bill-handle] (channel-pair)
          request-message (formats/encode-json->string
                            {:action "request" :token "abc"})
          relinquish-message (formats/encode-json->string
                            {:action "relinquish" :token "abc"})
          grant-message #(formats/encode-json->string
                           (build-message-to % "grant" "abc"))
          requested-message #(formats/encode-json->string
                           (build-message-to % "requested" "abc"))]
      (async-app karl-handle {})
      (async-app bill-handle {})
      (enqueue karl-channel "karl")
      (enqueue bill-channel "bill")
      (enqueue karl-channel request-message)
      (enqueue bill-channel request-message)
      (enqueue karl-channel relinquish-message)

      (with-next-item karl-channel karl-message
        (is (= karl-message (grant-message "0::::karl"))))
      (with-next-item karl-channel karl-message
        (is (= karl-message (requested-message "0::::karl"))))
      (with-next-item bill-channel bill-message
        (is (= bill-message (grant-message "0::::bill")))))))
