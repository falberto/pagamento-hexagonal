(ns picpago.domain.interceptor
  (:require [io.pedestal.interceptor.chain :as chain]
            [taoensso.timbre :as log]
            [io.pedestal.interceptor :as impl]
            [com.brunobonacci.mulog :as mu]
            [picpago.domain.models :as domain.models]))

(defn interceptor
  "Cria um interceptor com alguns helpers ja configurados"
  [{:keys [name enter leave error]
    :or   {enter identity
           error (fn [context ex]
                   ; Ao associar a exception na chave :io.pedestal.interceptor.chain/error, o pedestal irá continuar
                   ; procurando um handler (interceptor com chave :error).
                   ; See.: http://pedestal.io/reference/error-handling
                   (assoc context :io.pedestal.interceptor.chain/error ex))}}]
  (impl/interceptor
    {:name  name
     :enter (fn [context]
              (mu/with-context
                {:mulog/root-trace (or (-> context :tx-data :event/metadata :mulog/root-trace)
                                       (-> context :tx-data :command/metadata :mulog/root-trace))}
                (mu/trace
                  name [:on :enter
                        :correlation-id (-> context :tx-data :command/correlation-id)]
                  (tap> {:interceptor name
                         :on          :enter
                         :context     context})
                  (log/debug {:correlation-id (-> context :tx-data :command/correlation-id)
                              :msg            (str "Inicio da execucao do interceptor: " name)})
                  (domain.models/check-command-spec! (:tx-data context))
                  (enter context))))
     :leave (fn [context]
              (if leave
                (do
                  (mu/with-context
                    {:mulog/root-trace (or (-> context :tx-data :event/metadata :mulog/root-trace)
                                           (-> context :tx-data :command/metadata :mulog/root-trace))}
                    (mu/trace
                      name
                      [:on :leave
                       :correlation-id (-> context :tx-data :command/correlation-id)]
                      (tap> {:interceptor name
                             :on          :leave
                             :context     context})
                      (log/debug {:correlation-id (-> context :tx-data :command/correlation-id)
                                  :msg            (str "Fim da execucao do interceptor: " name)})
                      (leave context))))
                context))
     :error (fn [context ex-info]
              (mu/with-context
                {:mulog/root-trace (or (-> context :tx-data :event/metadata :mulog/root-trace)
                                       (-> context :tx-data :command/metadata :mulog/root-trace))}
                (mu/trace
                  name
                  [:on :error
                   :correlation-id (-> context :tx-data :command/correlation-id)
                   :exception ex-info])
                (tap> {:interceptor name
                       :on          :error
                       :context     context
                       :exception   ex-info})
                (log/error ex-info {:correlation-id (-> context :tx-data :command/correlation-id)
                                    :msg            (str "Falha na execucao do interceptor: " name)})
                (error context ex-info)))}))

(defn execute
  "Delegate to io.pedestal.interceptor.chain/execute."
  ([context]
   (chain/execute context))
  ([context interceptors]
   (chain/execute context interceptors)))
