(ns moose.in-memory-state
 (:use moose.collections
       moose.state-manipulations))

(def token-queues (ref {}))

(declare add-waiter-for-token new-holder)

(defn tokens-held-by [client]
  (map key (filter #(= client (first (val %)))  @token-queues)))

(defn reset-state! []
  (dosync
   (alter
    token-queues
    (fn [_] {}))))

(defn add-requestor
  ([token requestor token-queues]
  (dosync
    (alter
      token-queues
      #(add-waiter-for-token % requestor token))
    (token-add-report (get @token-queues token))))
  ([token requestor]
   (add-requestor token requestor token-queues)))

(defn waiters-for [token]
  (rest (get @token-queues token)))

(defn owner [token]
  (first (get @token-queues token)))

(defn remove-requestor
  ([token requestor]
   (dosync
     (let [waiters (get @token-queues token)
           without-requestor (vec (remove #(= requestor %) waiters))]
       (alter token-queues #(assoc % token without-requestor))
       (new-holder waiters without-requestor))))
  )

(defn- add-waiter-for-token [current-state requestor token]
  (let [waiters-for-token (get current-state token [])
        new-waiters (waiters-with-requestor waiters-for-token requestor)]
      (assoc current-state token new-waiters)))

(defn- new-holder [before after]
  (let [first-before (first before)
        first-after (first after)]
    (if (= first-before first-after)
      nil
      first-after)))
