(ns turtles.daemons)

;; Overload the printer for queues so they look like fish
(defmethod print-method clojure.lang.PersistentQueue [q, w]
  (print-method '<- w)
  (print-method (seq q) w)
  (print-method '-< w))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementation

(defrecord Daemon [ctx fns active?])

(defn- cycle-chain
  [{:keys [ctx fns active?] :as d}]
  (when active?
    (send *agent* cycle-chain))
  (doseq [f fns] (f ctx))
  d)

(defn- activate
  [d]
  (send *agent* cycle-chain)
  (assoc d :active? true))

(defn- deactivate
  [d]
  (assoc d :active? false))

(def latest-failure (atom nil))
(defn- agent-error-handler
  [a ex]
  (swap! latest-failure (.getMessage ex)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API

(defn activate-daemon [ctx fns]
  (let [deamon (->Daemon ctx fns true)
        dagent (agent deamon
                      :error-handler agent-error-handler)]
    (send dagent activate)))

(defn deactivate-daemon [d]
  (send d deactivate)
  true)
