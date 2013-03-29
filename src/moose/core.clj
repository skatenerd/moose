(ns moose.core
  (:use lamina.core
        aleph.http
        compojure.core
        (ring.middleware resource file-info params)
        (hiccup core page))
  (:require [compojure.route :as route]))



(def global-channel (named-channel :board))

(def token-to-user (atom {}))
(def token-to-channel (atom {}))

(defn subscribe-handler [channel request]
  (receive channel
    (fn [name]
      (siphon (map* #(str name ": " %) channel) global-channel)
      (siphon global-channel channel))))


(defn page [nom]
  (html5
   [:head
    (include-js "/js/core.js")]
   [:body
    "WAT"
    nom]))

(defn sync-app [channel request]
  (enqueue channel
      {:status 200
       :headers {"content-type" "text/html"}
       :body (page "ballto")}))

(def wrapped-sync-app
  (wrap-params (wrap-aleph-handler sync-app)))

(def wrapped-async-app
  (wrap-params (wrap-aleph-handler subscribe-handler)))

(defroutes my-routes
  (GET ["/subscribe/:key"] {}  wrapped-sync-app)
  (route/not-found wrapped-sync-app))



(defn app [channel request]
  (if (:websocket request)
    (subscribe-handler channel request)
    ((wrap-ring-handler (wrap-resource my-routes "public")) channel request)))




(defn -main [& args]
  (start-http-server app {:port 8080 :websocket true}))

