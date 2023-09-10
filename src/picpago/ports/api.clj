(ns picpago.ports.api
  "Entry point para as funcionalidades do sistema.

  Na literatura, aqui é a porta de entrada para a aplicação (drivers)."
  (:require [picpago.domain.interceptor :as interceptor]
            [taoensso.timbre :as log]
            [picpago.domain.core :as domain]))

(defn- sort-map [m]
  (try
    (cond
      (map? m)
      (into (sorted-map)
            (sort-by (comp name first)
                     (for [[k v] m]
                       [k (sort-map v)])))

      (sequential? m)
      (map sort-map m)

      :else m)
    (catch Exception _
      (log/warn "Fail to sort-map.")
      m)))


(defn- workflows []
  :user/criar-lojista domain/create-entity!
  :user/criar-pessoa domain/create-entity!)


(defn available-commands
  "Retorna a lista de comandos suportados pela API."
  []
  (sort (keys (workflows))))

(defn available-entities
  "Retorna a lista de entidades conhecidas pela API."
  []
  (sort (domain/available-entities)))

(defn execute!
  "Executa um processo imediatamente.

  Pode ser acionado diretamente em operações sincronas ou por adpters de comandos/eventos ocorridos na aplicação.

  See: (available-commands) ;; lista de comandos suportados"
  [{:command/keys [correlation-id type metadata] :as command}]
  (tap> {:fn   ::execute!
         :args command})
  (let [correlation-id (or correlation-id (random-uuid))
        workflow (get (workflows) type)
        id (or (:entity/id command) correlation-id)
        -command (assoc command
                   :entity/id id
                   :command/correlation-id correlation-id)]
    (when-not workflow
      (throw (ex-info (str "Workflow nao implementado para :command/type " type)
                      -command)))
    (let [context (interceptor/execute {:tx-data -command} workflow)]
      (assoc -command :command/status :executed
                      :command/data (get-in context [:tx-data :command/data])
                      :entity/id (get-in context [:tx-data :entity/id])))))



