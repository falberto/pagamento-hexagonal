(ns picpago.adapter.web
  (:require [malli.util :as mu]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as coercion.malli]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.coercion :as coercion]
            [reitit.openapi :as openapi]
            [reitit.swagger :as swagger]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]))


(defn handler-transaction
  [request]
  (println {:headers (select-keys request [:headers])
            :body    (slurp (:body request))
            :body1 (:body request)})

  {:status 200
   :body   {}})

(def app
  (ring/ring-handler
    (ring/router
      ["/api"
       ["/transaction" {:post
                        {:parameters {:body [:map
                                             {:closed true}
                                             [:value double?]
                                             [:payer int?]
                                             [:payee int?]]}
                         :handler handler-transaction}}]]
      ;; router data affecting all routes
      {:data {:coercion   (coercion.malli/create
                            {;; set of keys to include in error messages
                             :error-keys       #{#_:type #_:coercion #_:in #_:schema #_:value #_:errors :humanized #_:transformed}
                             ;; schema identity function (default: close all map schemas)
                             :compile          mu/closed-schema
                             ;; strip-extra-keys (effects only predefined transformers)
                             :strip-extra-keys true
                             ;; add/set default values
                             :default-values   true
                             ;; malli options
                             :options          nil})
              :muuntaja   m/instance
              :middleware [;; swagger & openapi
                           swagger/swagger-feature
                           openapi/openapi-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception/exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware]}})))

(comment
  (handler-transaction {})
  :a)