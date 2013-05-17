(ns moose.web.synchronous
  (:use
    compojure.core
    (hiccup core page)
    (ring.middleware resource file-info params)
    lamina.core
    aleph.http)
  (:require
    [moose.state :as state]
    [moose.config :as config]
    [compojure.route :as route]))

(declare page)

(defn sync-page-generator [channel request]
  (enqueue channel
      {:status 200
       :headers {"content-type" "text/html"}
       :body (page (get  (:query-params request) "name"))}))

(defn attempt-reset-state [password]
  (if (= password config/state-reset-password)
    (state/reset-state!)
    ))

(def wrapped-sync-page-generator
  (wrap-params (wrap-aleph-handler sync-page-generator)))

(defroutes sync-app-routes
  (GET "/reset-state/:password" [id]
    (attempt-reset-state id))
  (route/not-found wrapped-sync-page-generator))

(def sync-app
  (wrap-ring-handler (wrap-resource sync-app-routes "public")))

(defn- page [nom]
  (html5
   [:head
    (include-js "/js/core.js")]
   [:body
    "Hello,  "
    nom]))

