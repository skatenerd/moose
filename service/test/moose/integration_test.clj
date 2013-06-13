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
  `(let [~binding-name (wait-for-result (read-channel* ~channel :on-timeout "" :timeout 100))]
     ~@body))

(use-fixtures :each  (fn  [f] (state/reset-state!) (f)))

(def request-message
  (formats/encode-json->string {:action "request" :token "abc"}))

(def relinquish-message
  (formats/encode-json->string {:action "relinquish" :token "abc"}))

(def grant-message-to
  #(formats/encode-json->string (build-message-to % "grant" "abc")))

(def requested-message-to
  #(formats/encode-json->string
                               (token-requested-message %1 "abc" %2)))

(deftest
  pushes-notifications-to-clients
  (testing
    "two clients, both request, one relinquishes"
    (let [[karl-channel karl-handle] (channel-pair)
          [bill-channel bill-handle] (channel-pair)]
      (async-app karl-handle {})
      (async-app bill-handle {})
      (enqueue karl-channel "karl")
      (enqueue bill-channel "bill")
      (enqueue karl-channel request-message)
      (enqueue bill-channel request-message)
      (enqueue karl-channel relinquish-message)
      (with-next-item karl-channel karl-message)
      (with-next-item bill-channel bill-message)
      (with-next-item karl-channel karl-message
                      (is (= karl-message (grant-message-to "0::::karl"))))
      (with-next-item karl-channel karl-message
                      (is (= karl-message (requested-message-to "0::::karl" 1))))
      (with-next-item bill-channel bill-message
                      (is (= bill-message (grant-message-to "0::::bill"))))
      )))

(deftest
  shortening-line
  (testing
    "three clients request tokens, waiters learn when line shortens"
    (let [[karl-channel karl-handle] (channel-pair)
          [bill-channel bill-handle] (channel-pair)
          [friedrich-channel friedrich-handle] (channel-pair)
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
      (with-next-item friedrich-channel friedrich-message)
      (with-next-item friedrich-channel friedrich-message
                      (is (= friedrich-message (people-in-line "0::::friedrich" 1)))))))

(deftest
  state-reestablishment
  (testing
    "user is notified of tokens he owns when reconnecting"
    (let [[karl-channel karl-handle] (channel-pair)
          [second-karl-channel second-karl-handle] (channel-pair)]
      (async-app karl-handle {})
      (enqueue karl-channel "karl")
      (enqueue karl-channel request-message)
      (close karl-channel)
      (async-app second-karl-handle {})
      (enqueue second-karl-channel "karl")
      (with-next-item second-karl-channel karl-message
                      (is (= karl-message {:held ["abc"] :waiting '()})))
      )

    )
  )

;(deftest
;  cleanup
;  (testing
;    "owner of token relinquishes it by disconnecting"
;    (let [[karl-channel karl-handle] (channel-pair)
;          [bill-channel bill-handle] (channel-pair)]
;      (async-app karl-handle {})
;      (async-app bill-handle {})
;      (enqueue karl-channel "karl")
;      (enqueue bill-channel "bill")
;      (enqueue karl-channel request-message)
;      (enqueue bill-channel request-message)
;      (close karl-channel)
;      (with-next-item bill-channel bill-message
;        (is (= bill-message (grant-message-to "0::::bill")))))))

(deftest
  two-cannot-share-token
  (testing
    "two clients, both request, both relinquish, both request again"
    (let [[karl-channel karl-handle] (channel-pair)
          [bill-channel bill-handle] (channel-pair)]
      (async-app karl-handle {})
      (async-app bill-handle {})
      (enqueue karl-channel "karl")
      (enqueue bill-channel "bill")
      (enqueue karl-channel request-message)
      (enqueue bill-channel request-message)
      (enqueue karl-channel relinquish-message)
      (enqueue karl-channel request-message)
      (with-next-item karl-channel karl-message)
      (with-next-item karl-channel karl-message)
      (with-next-item karl-channel karl-message)
      (with-next-item karl-channel karl-message
                      (is (empty? karl-message))))))
