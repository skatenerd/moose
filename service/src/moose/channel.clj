(ns moose.channel
  (:use lamina.core))

(declare ignore)

(defn new-channel [name]
  (named-channel name ignore))



(defn- ignore [_]
  nil)

