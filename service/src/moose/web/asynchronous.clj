(ns moose.web.asynchronous
  (:use
    moose.core
    compojure.core
    (ring.middleware resource file-info params)
    lamina.core
    aleph.http)
  (:require
    [clojure.string :as string]
    [moose.message :as message]
    [compojure.route :as route]))

(declare client-name async-app)

(defn async-app [request-channel request]
  (receive
    request-channel
    (fn [client-specified-name]
      (let [the-name (client-name request client-specified-name)
            decoded-requests (map*
                               #(message/from-incoming-json
                                  %
                                  the-name)
                               request-channel)]
       (siphon
          (transform-to-events
            decoded-requests
            the-name)
          incoming-events)
        (siphon
          (map* message/encode-json (events-for-client the-name))
          request-channel)
        (on-closed request-channel #(handle-close the-name))


        ))))

(defn- client-name [request given-name]
  (let [ip (:remote-addr request)
        ip (hash ip)]
    (str ip "::::" given-name)))
