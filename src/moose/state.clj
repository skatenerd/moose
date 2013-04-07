(ns moose.state
 (:use moose.collections))

(def token-waiters (ref {}))

(declare already-waiting? new-waiters add-waiter-for-token)

(defn add-requestor [token requestor]
  (dosync
    (alter
      token-waiters
      #(add-waiter-for-token % requestor token))
    (first (get @token-waiters token))))

(defn remove-requestor [token requestor]
  (dosync
    (let [waiters (get @token-waiters token)
          without-requestor (remove #(= requestor %) waiters)]
      (alter token-waiters #(assoc % token without-requestor))
      {:before waiters
       :after without-requestor})))

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

