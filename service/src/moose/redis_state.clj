(ns moose.redis-state
  (:use moose.collections
        moose.state-manipulations)
  (:require [taoensso.carmine :as car]
            [aleph.formats :as formats]
            ))

(def pool         (car/make-conn-pool))
(def spec-server1 (car/make-conn-spec))

(declare get-user get-token)

(defmacro wcar [& body] `(car/with-conn pool spec-server1 ~@body))

(defn tokens-held-by [user]
  (let [token-query
        (wcar
          (car/atomically
            []
            (let [all-tokens (wcar (get-user user []))]
              (doall (map #(get-token %) all-tokens))
              )))
        ]
    (filter #(= user (first %)) token-query)
    )
  )
;token query -> all tokens

(defn tokens-awaited-by [user]
  (let [token-query
        (wcar
          (car/atomically
            []
            (let [all-tokens (wcar (get-user user []))]
              (doall (map #(get-token %) all-tokens))
              )))
        ]
    ;(filter identity (map #(.indexOf % ) all-tokens) token-query)
    )
  )

(defn queue-key [token]
  (formats/encode-json->string
    {
     :type :token-queue
     :value token
     }
    )
  )

(defn user-key [user]
  (formats/encode-json->string
    {
     :type :user-tokens
     :value user
     }
    )
  )

(defn get-user
  ([user]
   (car/get (user-key user)))
  ([user default]
   (or (get-user user) #{})
   ))

(defn set-user [user tokens]
  (car/set (user-key user) tokens))

(defn get-token
  ([token]
  (car/get (queue-key token)))
  ([token default]
   (or (get-token token) default)))

(defn set-token [token value]
  (car/set (queue-key token) value))

(defn owner [token]
  (first (wcar (get-token token))))

(defn add-requestor [token requestor]
  (let [token-add-results
        (wcar
          (car/atomically
            []
            (let [all-waiters (wcar (get-token token []))
                  new-waiters (waiters-with-requestor all-waiters requestor)
                  tokens-for-user (wcar (get-user requestor))
                  new-tokens (conj tokens-for-user token)]
              (set-token token new-waiters)
              (set-user requestor new-tokens)
              (get-token token)
              )))
       new-owners
       (last token-add-results)]
   (token-add-report new-owners)))

(defn remove-requestor [token requestor]
  (let [first-owner
        (owner token)
        token-remove-results
        (wcar
          (car/atomically
            []
            (let [all-of-them (wcar (get-token token))
                  all-of-them (or all-of-them [])
                  waiters-without-requestor (vec (remove #(= requestor %) all-of-them))]
              (set-token token waiters-without-requestor)
              (get-token token)
              )))
        waiters-without-requestor (last token-remove-results)]
    (if (not (= first-owner (first waiters-without-requestor)))
      (first (last token-remove-results)))))

(defn waiters-for [token]
  (let [owner-and-waiters
        (wcar
          (get-token token))]
    (rest owner-and-waiters)))

(defn reset-state! []
  (wcar
    (doall
      (for [token (wcar (car/keys "*"))]
        (car/del token)))))

