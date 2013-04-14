(ns moose.core
  (:use
    compojure.core
    (hiccup core page)
    lamina.core
    moose.collections
    aleph.http)
  (:require
    [aleph.formats :as formats]
    [compojure.route :as route]
    [moose.state :as state]
    [moose.message :as message]))

(declare new-holder transform-request-to-event transform-to-outgoing-events handle-request-event handle-relinquish-event register-channel-for-token)

(defn ignore [_]
  nil)

(def incoming-events (named-channel "incoming" ignore))
(def outgoing-events (named-channel "outgoing-events" ignore))
(def grants (named-channel "grants" ignore))
(def requests (named-channel "requests" ignore))

(defn transform-to-events [incoming-request-channel client-name]
  (map*
    #(transform-request-to-event % client-name)
    incoming-request-channel))

(defn events-for-client [client-specified-name]
  (filter* (message/for-user? client-specified-name) outgoing-events))

(defn- transform-request-to-event [action humanoid-namezoid]
  (let [to-enqueue action]
    (register-channel-for-token (:token action))
    to-enqueue))

(defn- register-channel-for-token [token]
  (named-channel
    token
    (fn [channel]
      (siphon (filter* (message/for-token? token) incoming-events) channel)
      (siphon (transform-to-outgoing-events channel) outgoing-events))))

(defn- transform-to-outgoing-events [token-channel]
  (map* (fn [message]
          (let [sender (message/sender message)
                token (message/token message)]
           (case (:event message)
              "request" (handle-request-event sender token)
              "relinquish" (handle-relinquish-event sender token))))
        token-channel))

(defn- handle-relinquish-event [relinquisher token]
  (let [new-holder (state/remove-requestor token relinquisher)]
    (if new-holder
      (message/build-message-to new-holder :grant token))))

(defn- handle-request-event [requestor token]
  (let [add-results (state/add-requestor token requestor)
        holder (:holder add-results)
        queue-length (:queue-length add-results)
        got-the-token? (= holder requestor)]
    (if got-the-token?
      (message/build-message-to requestor :grant token)
      (message/token-requested-message holder token queue-length))))
