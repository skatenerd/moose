(ns moose.web.synchronous
  (:use
    compojure.core
    (hiccup core page)
    (ring.middleware resource file-info params reload)
    lamina.core
    aleph.http)
  (:require
    [compojure.route :as route]))

(declare page)

(defn sync-page-generator [channel request]
  (enqueue channel
      {:status 200
       :headers {"content-type" "text/html"}
       :body (page (get  (:query-params request) "name"))}))

(def wrapped-sync-page-generator
  (wrap-reload (wrap-params (wrap-aleph-handler sync-page-generator)) '(moose.core)))

(defroutes sync-app-routes
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

