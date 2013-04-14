(ns moose.message
  (:use
    compojure.core
    (hiccup core page)
    lamina.core
    aleph.http)
  (:require
    [aleph.formats :as formats]
    [compojure.route :as route]))

(declare valid? decode-json)

(defn from-incoming-json [payload sender]
  (let [decoded (decode-json payload)]
    (if (valid? payload)
      {:sender sender
       :event (:action decoded)
       :token (:token decoded)})))

(defn build-message-to
 ([user action token options]
  (merge
    {:recipient user
     :event action
     :token token}
    options))
  ([user action token]
   (build-message-to user action token {})))

(defn token-requested-message [user token queue-length]
  (build-message-to user :requested token {:queue-length queue-length}))

(defn people-in-line-message [user token queue-length]
  (build-message-to user :queue-length token {:queue-length queue-length}))

(defn build-message-from [user action token]
  {:sender user
   :event action
   :token token})

(defn sender [message]
  (:sender message))

(defn recipient [message]
  (:recipient message))

(defn token [message]
  (:token message))

(defn event [message]
  (:event message))

(defn for-user? [user]
  #(= (:recipient %) user))

(defn for-token? [token]
  #(= (:token %) token))

(defn- valid? [message]
  true)

(defn decode-json [message]
  (formats/decode-json message))


(defn encode-json [message]
  (formats/encode-json->string message))
