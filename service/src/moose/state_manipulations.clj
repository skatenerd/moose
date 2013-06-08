(ns moose.state-manipulations
  (:use moose.collections)
  )

(defn already-waiting? [waiters requestor]
  (iterative-contains? requestor waiters))

(defn new-waiters [waiters-for-token requestor]
  (let []
    (if (already-waiting? waiters-for-token requestor)
      waiters-for-token
      (conjv waiters-for-token requestor))))

(defn token-add-report [waiters]
  {:holder (first waiters)
   :queue-length (dec (count waiters))})

