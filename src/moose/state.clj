(ns moose.state
 (:use moose.collections))

(def token-queues (ref {}))

(declare already-waiting? new-waiters add-waiter-for-token new-holder token-add-report)

(defn add-requestor
  ([token requestor token-queues]
  (dosync
    (alter
      token-queues
      #(add-waiter-for-token % requestor token))
    (token-add-report token requestor token-queues)))
  ([token requestor]
   (add-requestor token requestor token-queues)))

(defn waiters-for [token]
  (rest (get @token-queues token)))

(defn remove-requestor
  ([token requestor token-queues]
   (dosync
     (let [waiters (get @token-queues token)
           without-requestor (remove #(= requestor %) waiters)]
       (alter token-queues #(assoc % token without-requestor))
       (new-holder waiters without-requestor))))
  ([token requestor]
   (remove-requestor token requestor token-queues)))

(defn- token-add-report [token requestor token-queues]
  {:holder (first (get @token-queues token))
   :queue-length (dec (count (get @token-queues token)))})

(defn- add-waiter-for-token [current-state requestor token]
  (let [waiters-for-token (get current-state token [])
        new-waiters (new-waiters waiters-for-token requestor)]
      (assoc current-state token new-waiters)))

(defn- new-waiters [waiters-for-token requestor]
  (let []
    (if (already-waiting? waiters-for-token requestor)
      waiters-for-token
      (conjv waiters-for-token requestor))))

(defn- already-waiting? [waiters requestor]
  (iterative-contains? requestor waiters))

(defn- new-holder [before after]
  (let [first-before (first before)
        first-after (first after)]
    (if (= first-before first-after)
      nil
      first-after)))
