(ns moose.web
  (:use
    moose.core
    moose.web.synchronous
    moose.web.asynchronous
    compojure.core
    (hiccup core page)
    (ring.middleware resource file-info params)
    aleph.http)
  (:require
    [clojure.string :as string]
    [moose.message :as message]
    [compojure.route :as route]))

(declare app)

(defn- app [channel request]
  (if (:websocket request)
    (async-app channel request)
    (sync-app channel request)))

(defn -main [& args]
  (start-http-server app {:port 8080 :websocket true}))
