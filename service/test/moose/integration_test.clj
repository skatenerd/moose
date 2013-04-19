(ns moose.integration-test
  (:use clojure.test
        lamina.core
        lamina.viz
        moose.message
        moose.web.asynchronous)
  (:require
    [moose.state :as state]
    [aleph.formats :as formats]))

(defmacro with-next-item [channel binding-name & body]
  `(let [~binding-name (wait-for-result (read-channel ~channel) 100)]
     ~@body))

(use-fixtures :each  (fn  [f] (state/reset-state!) (f)))

(deftest
  pushes-notifications-to-clients
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
                               (token-requested-message %1 "abc" %2))]
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
                      (is (= karl-message (requested-message "0::::karl" 1))))
      (with-next-item bill-channel bill-message
                      (is (= bill-message (grant-message "0::::bill")))))))
(deftest
  shortening-line
  (testing
    "three clients request tokens, waiters learn when line shortens"
    (let [[karl-channel karl-handle] (channel-pair)
          [bill-channel bill-handle] (channel-pair)
          [friedrich-channel friedrich-handle] (channel-pair)
          request-message (formats/encode-json->string
                            {:action "request" :token "abc"})
          relinquish-message (formats/encode-json->string
                               {:action "relinquish" :token "abc"})
          people-in-line #(formats/encode-json->string
                            (people-in-line-message %1 "abc" %2))]
      (async-app karl-handle {})
      (async-app bill-handle {})
      (async-app friedrich-handle {})
      (enqueue karl-channel "karl")
      (enqueue bill-channel "bill")
      (enqueue friedrich-channel "friedrich")
      (enqueue karl-channel request-message)
      (enqueue bill-channel request-message)
      (enqueue friedrich-channel request-message)
      (enqueue karl-channel relinquish-message)
      (with-next-item friedrich-channel friedrich-message
                      (is (= friedrich-message (people-in-line "0::::friedrich" 1))))


      )))

(deftest
  cleanup
  (testing
    "owner of token relinquishes it by disconnecting"
    (let [[karl-channel karl-handle] (channel-pair)
          [bill-channel bill-handle] (channel-pair)
          request-message (formats/encode-json->string
                            {:action "request" :token "abc"})
          relinquish-message (formats/encode-json->string
                               {:action "relinquish" :token "abc"})
          grant-message #(formats/encode-json->string
                           (build-message-to % "grant" "abc"))
          ]
      (async-app karl-handle {})
      (async-app bill-handle {})
      (enqueue karl-channel "karl")
      (enqueue bill-channel "bill")
      (enqueue karl-channel request-message)
      (enqueue bill-channel request-message)
      (close karl-channel)
      (with-next-item bill-channel bill-message
        (is (= bill-message (grant-message "0::::bill"))))
)))

