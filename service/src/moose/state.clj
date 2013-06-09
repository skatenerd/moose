(ns moose.state
 (:require
   [moose.in-memory-state :as in-memory]
   [moose.redis-state :as redis]
   [moose.config :as config]
   ))

(defmulti tokens-held-by (fn [_] @config/state-implementation))

(defmethod tokens-held-by :in-memory [client]
  (in-memory/tokens-held-by client))

(defmethod tokens-held-by :redis [client]
  (redis/tokens-held-by client))

(defmulti reset-state! (fn [] @config/state-implementation))

(defmethod reset-state! :in-memory []
  (in-memory/reset-state!))

(defmethod reset-state! :redis []
  (redis/reset-state!))


(defmulti add-requestor (fn [& _] @config/state-implementation))

(defmethod add-requestor :in-memory [token requestor]
  (in-memory/add-requestor token requestor))

(defmethod add-requestor :redis [token requestor]
  (redis/add-requestor token requestor))

(defmulti remove-requestor (fn [& _] @config/state-implementation))

(defmethod remove-requestor :in-memory [token requestor]
  (in-memory/remove-requestor token requestor))

(defmethod remove-requestor :redis [token requestor]
  (redis/remove-requestor token requestor))

(defmulti waiters-for (fn [_] @config/state-implementation))

(defmethod waiters-for :in-memory [token]
  (in-memory/waiters-for token))

(defmethod waiters-for :redis [token]
  (redis/waiters-for token))

(defmulti owner (fn [_] @config/state-implementation))

(defmethod owner :in-memory [token]
  (in-memory/owner token))

(defmethod owner :redis [token]
  (redis/owner token))
