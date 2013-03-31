(ns moose.core
  (:use
    compojure.core
    (hiccup core page)
    lamina.core
    (ring.middleware resource file-info params reload)
    aleph.http)
  (:require
    [aleph.formats :as formats]
    [compojure.route :as route]
    [moose.message :as message]
    )
  )

(declare transform-to-outgoing-events process-request process-relinquish)

(def token-waiters (ref {}))

(def incoming-events (named-channel "incoming"))
(def outgoing-events (named-channel "outgoing-events"))
(def grants (named-channel "grants"))
(def requests (named-channel "requests"))


(defn filter-for-user [user stream]
  (filter* #(= (:client (or % {})) user) stream))
(defn filter-for-token [token stream]

  (filter* #(= (:token (or % {})) token) stream))

(defn register-token-channel [token _]
  (named-channel
    token
    (fn [channel]
      (siphon (filter-for-token token incoming-events) channel)
      (siphon (transform-to-outgoing-events channel) outgoing-events))))

(defn request-to-event [action humanoid-namezoid]
  (let [to-enqueue (merge action {:client humanoid-namezoid})]
    (register-token-channel (:token action) humanoid-namezoid)
    to-enqueue))

(defn requests-to-events [request-channel remote-address humanoid-namezoid]
  (let [client-name (str remote-address "::::" humanoid-namezoid)]
    (map*
      #(request-to-event % humanoid-namezoid)
      request-channel)))

(defn events-to-client [client-specified-name]
  (filter-for-user client-specified-name outgoing-events))

(def relinquish-stream
  (filter* (fn [data]
             (let [action (:action data)]
               (= action "relinquish"))
             )
           incoming-events))

(def request-stream
  (filter* (fn [data]
             (let [action (:action data)]
               (= action "request")))
           incoming-events))

(defn- iterative-contains? [value items]
  (some #(= % value) items))

(defn- conjv [coll item]
  (into [] (conj coll item))
  )

(defn already-waiting? [waiters requestor]
  (iterative-contains? requestor waiters))

(defn add-requestor [token requestor]
  (dosync
    (alter token-waiters (fn [waiters]
                           (let [waiters-for-token (get waiters token [])
                                 new-waiters (if (already-waiting? waiters-for-token requestor)
                                               waiters-for-token
                                               (conjv waiters-for-token requestor))]
                             (assoc waiters token new-waiters))))
    (first (get @token-waiters token))))

(defn remove-requestor [token requestor]
  (dosync
    (let [waiters (get @token-waiters token)
          without-requestor (remove #(= requestor %) waiters)]
      (alter token-waiters #(assoc % token without-requestor))
      {:before waiters
       :after without-requestor})))

(defn transform-to-outgoing-events [token-channel]
  (map* (fn [request]
          (let [token   (:token request)
                client  (:client request)
                action  (:action request)]
            (case action
              "request" (process-request token client action)
              "relinquish" (process-relinquish token client action)
              )))
        token-channel
        ))

(defn process-request [token client action]
  (let [holder (add-requestor token client)
        got-the-token? (= holder client)]
    (if got-the-token?
           {:client client
            :event :grant
            :token token}
           {:client holder
            :event :requested
            :token token}
           )))

(defn process-relinquish [token client action]
  (let [relinquisher client
        foo  (remove-requestor token relinquisher)
        before (:before foo)
        after (:after foo)
        new-holder? (not (= (first before) (first after))) ]
    (if new-holder?
      {:client (first after)
       :event :grant
       :token token})))
