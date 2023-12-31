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
  {:user/criar-lojista [domain/create-entity!]
   :user/create-pessoa [domain/create-entity!]})

;(defn available-commands
;  "Retorna a lista de comandos suportados pela API."
;  []
;  (sort (keys (workflows))))
;
;(defn available-entities
;  "Retorna a lista de entidades conhecidas pela API."
;  []
;  (sort (domain/available-entities)))

(defn execute!
  "Executa um processo imediatamente.

  Pode ser acionado diretamente em operações sincronas ou por adpters de comandos/eventos ocorridos na aplicação.

  See: (available-commands) ;; lista de comandos suportados"
  [{:command/keys [correlation-id type] :as command}]
  (tap> {:fn   ::execute!
         :args command})
  (let [-correlation-id (or correlation-id (throw (ex-info "correlation id must be informed." {:command command})))
        workflow (get (workflows) type)
        #_#_id (or (:entity/id command) -correlation-id)
        -command (assoc command
                   ;:entity/id id
                   :command/correlation-id -correlation-id)]
    (when-not workflow
      (throw (ex-info (str "workflow not found for command/type " type)
                      -command)))
    (let [context (interceptor/interceptor {:tx-data -command} workflow)]
      (assoc -command :command/status :executed
                      :command/data (get-in context [:tx-data :command/data])
                      :entity/id (get-in context [:tx-data :entity/id])))))



