(ns moose.state
 (:use moose.collections))

(def token-waiters (ref {}))

(declare already-waiting? new-waiters add-waiter-for-token new-holder)

(defn add-requestor
  ([token requestor token-waiters]
  (dosync
    (alter
      token-waiters
      #(add-waiter-for-token % requestor token))
    (first (get @token-waiters token))))
  ([token requestor]
   (add-requestor token requestor token-waiters)))

(defn remove-requestor
  ([token requestor token-waiters]
   (dosync
     (let [waiters (get @token-waiters token)
           without-requestor (remove #(= requestor %) waiters)]
       (alter token-waiters #(assoc % token without-requestor))
       (new-holder waiters without-requestor))))
  ([token requestor]
   (remove-requestor token requestor token-waiters)))

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
