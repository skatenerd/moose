(ns moose.core
  (:use lamina.core
        aleph.http
        compojure.core
        (ring.middleware resource file-info params reload)
        (hiccup core page))
  (:require
      [lamina.core.named :as named]
      [aleph.formats :as formats]
    )
  (:require [compojure.route :as route]))

(def token-holders (atom {}))
(def token-waiters (atom {}))

(defn record-token-waiter [token waiter-name])

(def highway-channel (named-channel "highway"))

;fix parallelism
(defn get-token-channel [channel-name]
  (named-channel channel-name))

(defn transformer-for-client [client-name]
  (fn [stream]
   (map* (fn [data] (prn "HAHAHAHA") (prn (type data)) data) stream)))

(defn subscribe-handler [request-channel request]
  (receive request-channel
    (fn [client-name]
      (siphon (map*
                  (fn [action]
                    (let [decoded (formats/decode-json action)
                          action (:action decoded)
                          token (:token decoded) ]
                      (record-token-waiter token client-name)
                      (str { :action action
                             :token token
                             :channel client-name
                           })
                       ))
                  request-channel)
                highway-channel)
        (siphon
          ((transformer-for-client client-name) highway-channel)
          request-channel)
        )
      ))


(defn page [nom]
  (html5
   [:head
    (include-js "/js/core.js")]
   [:body
    "WAT  "
    nom]))

(defn sync-app [channel request]
  (enqueue channel
      {:status 200
       :headers {"content-type" "text/html"}
       :body (page "ballto")}))

(def wrapped-sync-app
  (wrap-reload (wrap-params (wrap-aleph-handler sync-app)) '(moose.core)))

(def wrapped-async-app
  (wrap-reload (wrap-params (wrap-aleph-handler subscribe-handler))))

(defroutes my-routes
  (GET ["/subscribe/:key"] {}  wrapped-async-app)
  (route/not-found wrapped-sync-app))

(defn app [channel request]
  (if (:websocket request)
    (subscribe-handler channel request)
    ((wrap-ring-handler (wrap-resource my-routes "public")) channel request)))

(defn -main [& args]
  (start-http-server app {:port 8080 :websocket true}))

