(ns turtles.daemons)

(defrecord Daemon [ctx op active?])

(defn- run
  [{:keys [ctx op active?] :as d}]
  (when active?
    (send *agent* run)
    (op ctx))
  d)

(defn- activate
  [d]
  (send *agent* run)
  (assoc d :active? true))

(defn- deactivate
  [d]
  (assoc d :active? false))

;; TODO: Improve daemon error handling.
(def latest-failure (atom nil))
(defn- agent-error-handler
  [a ex]
  (swap! latest-failure (.getMessage ex)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API

(defn activate-daemon
  [ctx op]
  (let [deamon (->Daemon ctx op true)
        dagent (agent deamon :error-handler agent-error-handler)]
    (send dagent run)))

(defn deactivate-daemon
  [dagent]
  (send dagent deactivate)
  (await dagent)
  dagent)

(defn restart-daemon
  [dagent]
  (send dagent activate))
