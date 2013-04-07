(ns moose.message
  (:use
    compojure.core
    (hiccup core page)
    lamina.core
    (ring.middleware resource file-info params reload)
    aleph.http)
  (:require
    [aleph.formats :as formats]
    [compojure.route :as route]))

(defn valid? [message]
  true
  )

(defn decode-json [message]
  (prn message)
  (if (valid? message)
    (formats/decode-json message)))

(defn for-user? [user]
  (prn user)
  #(= (:client %) user))

(defn for-token? [token]
  #(= (:token %) token))
