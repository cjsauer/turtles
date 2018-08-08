(ns turtles.datascript-sandbox
  (:require [datascript.core :as d]
            [quil.core :as q]
            [turtles.math :as math]))

(def world-size 1000)

(defn now
  []
  (System/currentTimeMillis))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Database

(defn rand-color
  []
  (into [] (repeatedly 3 #(rand-int 256))))

(defn rand-pos
  [x y]
  [(rand-int x) (rand-int y)])

(defn transact!
  "Same as d/transact! except also decorates the transaction with
  :db/txInstant metadata, whose value is the current time in milliseconds."
  [conn tx-data]
  (let [current-time (System/currentTimeMillis)
        tx-instant [[:db/add :db/current-tx :db/txInstant current-time]]]
   (d/transact! conn (concat tx-data tx-instant))))

(defn- create-turtle
  []
  {:color (rand-color)
   :position (rand-pos world-size world-size)
   :velocity [(dec (rand 2)) (dec (rand 2))]})

(defn create-turtles!
  [conn n]
  (transact! conn (for [_ (range n)]
                    (create-turtle)))
  conn)

(defn create-universe
  []
  (d/create-conn))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Daemons

(defprotocol IAutonomous
  (observe [self db] "Returns some filtered view of db from the perspective of self.")
  (process [self observation] "Returns transaction data based on the observation results."))

(defn- velocity-tx
  [db db-id dt]
  (let [self (d/entity db db-id)
        new-pos (math/coord+ (:position self)
                             (math/coord* (:velocity self) dt))]
    {:db/id db-id
     :position new-pos}))

(def velocity-ms (atom (System/currentTimeMillis)))
(defn velocity-daemon
  [{:keys [conn] :as ctx}]
  (send *agent* velocity-daemon)
  (let [dt (- (now) @velocity-ms)
        db @conn
        es (d/q '[:find [?e ...]
                  :where
                  [?e :position]
                  [?e :velocity]]
                db)
        txs (map #(velocity-tx db % 1.0) es)]
    (d/transact! conn txs)
    (reset! velocity-ms (now))
    ctx))

(defn start-daemon
  [conn daemon-fn]
  (let [daemon (agent {:conn conn})]
    (send daemon daemon-fn)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UI

(defn render-universe
  [db]
  (let [renderables (d/q '[:find ?color ?pos
                           :where
                           [?e :color ?color]
                           [?e :position ?pos]]
                         db)]
    (q/fill 0 0 0)
    (q/rect 0 0 world-size world-size)
    (doseq [[[r g b] [x y]] renderables]
      (q/fill r g b)
      (q/ellipse x y 10 10))))

(defn- setup-sketch
  []
  (q/background 0)
  (q/frame-rate 30))

(defn start-sketch
  [conn]
  (q/sketch
   :title "Turtles"
   :setup setup-sketch
   :draw #(render-universe @conn)
   :size [world-size world-size]))

(comment

  (do
    (def conn (create-universe))
    (def ui (start-sketch conn))
    (create-turtles! conn 500)
    true)

  )
