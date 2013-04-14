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
    (let [[karl-input karl-output] (channel-pair)
          [bill-input bill-output] (channel-pair)
          request-message (formats/encode-json->string
                            {:action "request" :token "abc"})
          relinquish-message (formats/encode-json->string
                            {:action "relinquish" :token "abc"})
          grant-message #(formats/encode-json->string
                           (build-message-to % "grant" "abc"))
          requested-message #(formats/encode-json->string
                           (build-message-to % "requested" "abc"))]
      (async-app karl-output {})
      (async-app bill-output {})
      (enqueue karl-input "karl")
      (enqueue bill-input "bill")
      (enqueue karl-input request-message)
      (enqueue bill-input request-message)
      (enqueue karl-input relinquish-message)

      (with-next-item karl-input karl-message
        (is (= karl-message (grant-message "0::::karl"))))
      (with-next-item karl-input karl-message
        (is (= karl-message (requested-message "0::::karl"))))
      (with-next-item bill-input bill-message
        (is (= bill-message (grant-message "0::::bill")))))))
