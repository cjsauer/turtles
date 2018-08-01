(ns turtles.daemons
  (:import [clojure.lang PersistentQueue]))

;; Overload the printer for queues so they look like fish
(defmethod print-method clojure.lang.PersistentQueue [q, w]
  (print-method '<- w)
  (print-method (seq q) w)
  (print-method '-< w))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementation

(defrecord Daemon [world patch turtle fn-chain active?])

(defn- deactivate
  [d]
  (assoc d :active? false))

(defn- cycle-chain
  [{:keys [active? fn-chain world patch turtle] :as d}]
  (when active?
    (send *agent* cycle-chain))
  (if-let [nextf (peek fn-chain)]
    (do (nextf world patch turtle)
        (assoc d :fn-chain (conj (pop fn-chain) nextf)))
    (deactivate d)))

(def latest-failure (atom nil))
(defn- agent-error-handler
  [a ex]
  (swap! latest-failure (.getMessage ex)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API

(defn activate-daemon [world patch turtle & [fns]]
  (let [fn-chain (into PersistentQueue/EMPTY fns)
        deamon (->Daemon world patch turtle fn-chain true)
        dagent (agent deamon
                      :error-handler agent-error-handler)]
    (send dagent cycle-chain)))

(defn deactivate-daemon [d]
  (send d deactivate)
  true)
