(ns vimsical.sim.util.http
  (:require
   [aleph.http :as http]
   [aleph.http.client-middleware :as middleware]
   [byte-streams :as bs]
   [vimsical.sim.util.manifold :as m]
   [vimsical.sim.util.transit :as t]))

(defn encode-request
  [req]
  (-> req
      (assoc-in [:headers "content-type"] "application/transit+json")
      (update :body t/transit-write-string)))

(defn wrap-req-transit-body
  [handler]
  (fn [req]
    (handler
     (encode-request req))))

(def defaults
  {:request-method :post
   :middleware     wrap-req-transit-body
   :as             :transit+json
   :transit-opts   {:encode t/writer-opts :decode t/reader-opts}})

(defn req-chan
  [req]
  (m/deferred->chan
    (http/request
     (merge defaults req))))


