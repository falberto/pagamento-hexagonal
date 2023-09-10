(ns picpago.adapter.web
  (:require [malli.util :as mu]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as coercion.malli]
            [reitit.openapi :as openapi]
            [reitit.ring :as ring]
            [reitit.ring.coercion]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [taoensso.timbre :as log]
            [picpago.ports.api :as api]
            [reitit.swagger :as swagger]))

(defn success
  ([]
   (success nil))
  ([payload]
   {:status 200
    :body   payload}))

(defn- accepted
  [payload]
  {:status 202
   :body   payload})

(defn- no-content
  []
  {:status 204})

(defn- bad-request
  [payload]
  {:status 400
   :body   payload})

(defn- conflict
  [payload]
  {:status 409
   :body   {:message payload}})

(defn- not-found
  []
  {:status 404
   :body   {:message "Registro não encontrado."}})

(defn handler-transaction!
  [request]
  {:status 200
   :body   (-> request :parameters :body)})

(defn handler-create-user!
  [request]
  (let [result (api/execute! {:entity/type            :user
                              :command/type           :user/criar-pessoa
                              :command/data           (-> request :parameters :body)
                              :command/metadata       {}
                              :command/correlation-id {}})]
    (success {:id (:entity/id result)})))


(defn handler-show-user!
  [request]
  {:status 200
   :body   (-> request :parameters :path :id)})

(def app
  (ring/ring-handler
    (ring/router
      ["/api"
       ["/user/:id"
        {:get {:parameters {:path [:map
                                   [:id string?]]}
               :handler    handler-show-user!}}]
       ["/user"
        {:post {:parameters {:body [:map
                                    [:nome string?]
                                    [:cpf string?]
                                    [:email string?]
                                    [:senha string?]]}
                :handler    handler-transaction!}}]
       ["/transaction" {:post
                        {:parameters {:body [:map
                                             [:value double?]
                                             [:payer int?]
                                             [:payee int?]]}
                         :handler    handler-transaction!}}]]
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
                           coercion/coerce-request-middleware
                           ;; multipart
                           multipart/multipart-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :urls [{:name "swagger", :url "swagger.json"}
                         {:name "openapi", :url "openapi.json"}]
                  :urls.primaryName "openapi"
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(comment
  (handler-transaction {})
  (user/reset!)
  :a)