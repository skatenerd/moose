(ns moose.redis-state
  (:use moose.collections
        moose.state-manipulations
        )
  (:require [taoensso.carmine :as car])
  )

(def pool         (car/make-conn-pool))
(def spec-server1 (car/make-conn-spec))
(defmacro wcar [& body] `(car/with-conn pool spec-server1 ~@body))

(defn tokens-held-by [user]
  [])

(defn owner [token]
  (first (wcar (car/get token))))

(defn add-requestor [token requestor]
  (let [token-add-results
        (wcar
          (car/atomically
            []
            (let [all-waiters (wcar (car/get token))
                  all-waiters (or all-waiters [])
                  new-waiters (waiters-with-requestor all-waiters requestor)]
              (car/set token new-waiters)
              (car/get token)
              )))
       new-owners
       (first (last token-add-results))]
   (token-add-report new-owners)))

(defn remove-requestor [token requestor]
  (let [first-owner
        (owner token)
        token-remove-results
        (wcar
          (car/atomically
            []
            (let [all-of-them (wcar (car/get token))
                  all-of-them (or all-of-them [])
                  waiters-without-requestor (vec (remove #(= requestor %) all-of-them))]
              (car/set token waiters-without-requestor)
              (car/get token)
              )))
        waiters-without-requestor (last token-remove-results)]
    (if (not (= first-owner (first waiters-without-requestor)))
      (first (last token-remove-results)))))

(defn waiters-for [token]
  (let [owner-and-waiters
        (wcar
          (car/get token))]
    (rest owner-and-waiters)))

(defn reset-state! []
  (wcar
    (doall
      (for [token (wcar (car/keys "*"))]
        (car/del token)))))

