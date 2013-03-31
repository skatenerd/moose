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

(defn decode-json [message]
  (prn message)
   (formats/decode-json message))

;(defn for-user? [message-string]
;  (= (:client (read-string (or message-string "{}"))) user))
