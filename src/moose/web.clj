(ns moose.web
  (:use
    moose.core
    compojure.core
    (hiccup core page)
    (ring.middleware resource file-info params reload)
    lamina.core
    aleph.http)
  (:require
    [clojure.string :as string]
    [moose.message :as message]
    [compojure.route :as route]))

(defn page [nom]
  (html5
   [:head
    (include-js "/js/core.js")]
   [:body
    "Hello,  "
    nom]))

(defn sync-app [channel request]
  (enqueue channel
      {:status 200
       :headers {"content-type" "text/html"}
       :body (page (get  (:query-params request) "name"))}))

(defn client-name [request given-name]
  (let [ip (:remote-addr request)
        ip (hash ip)]
    (str ip "::::" given-name)))

(defn subscribe-handler [request-channel request]
  (receive
    request-channel
    (fn [client-specified-name]
      (let [the-name (client-name request client-specified-name)
            decoded-requests (map* message/decode-json request-channel)]
       (siphon
          (transform-to-events
            decoded-requests
            the-name)
          incoming-events)
        (siphon
          (map* str (events-for-client the-name))
          request-channel)))))

(def wrapped-async-app
  (wrap-reload (wrap-params (wrap-aleph-handler subscribe-handler))))

(def wrapped-sync-app
  (wrap-reload (wrap-params (wrap-aleph-handler sync-app)) '(moose.core)))

(defroutes my-routes
  (GET ["/subscribe/:key"] {}  wrapped-async-app)
  (route/not-found wrapped-sync-app))

(defn app [channel request]
  (if (:websocket request)
    (subscribe-handler channel request)
    ((wrap-ring-handler (wrap-resource my-routes "public")) channel request)))

(defn -main [& args]
  (start-http-server app {:port 8080 :websocket true}))


