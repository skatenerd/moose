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

(defn add-requestor [token requestor]
  (wcar
    (car/atomically
      []
      (let [all-of-them (wcar (car/get token))
            all-of-them (or all-of-them [])
            new-waiters (new-waiters all-of-them requestor)]
        (car/set token new-waiters))))
  (token-add-report (wcar (car/get token))))

(defn remove-requestor [token requestor]
  "HAHA"
  )

(defn waiters-for [token]
  (let [my-thing
        (wcar
          (car/get token))]
    (rest my-thing))
  )

(defn reset-state! []
  (wcar
    (doall
      (for [token (wcar (car/keys "*"))]
        (car/del token)))))

