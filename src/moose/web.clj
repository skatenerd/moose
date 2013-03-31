(ns moose.web
  (:use
    moose.core
    compojure.core
    (hiccup core page)
    (ring.middleware resource file-info params reload)
    lamina.core
    aleph.http)
  (:require
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

(defn subscribe-handler [request-channel request]
  (receive
    request-channel
    (fn [client-specified-name]
      (let [decoded-requests (map* message/decode-json request-channel)]
        (siphon (requests-to-events decoded-requests (:remote-addr request) client-specified-name) incoming-events)
        (siphon (map* str (events-to-client client-specified-name))  request-channel)))))

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


