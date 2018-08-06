(ns turtles.core
  (:require [turtles.daemons :as d]
            [turtles.protocols :as proto
             :refer
             [add-turtle!
              color
              coord
              id
              patch-at
              patch-seq
              forward
              backward
              right
              left
              select-turtles
              turtles
              update-patch!
              update-turtle!]]
            [turtles.turtle :as t]
            [turtles.ui :as ui]
            [turtles.world :as w :refer [neighbors]]
            [turtles.world.rectangular :as wrect]))

(defonce world (atom nil))
(defonce daemons (atom #{}))

(defn start-new-world
  [sizex sizey]
  (reset! world (wrect/make-rectangular-world sizex sizey))
  (ui/start-sketch @world))

(defn rand-coord
  [w]
  (let [[maxx maxy] (w/bounds w)]
    [(rand-int maxx) (rand-int maxy)]))

(defn create-turtles
  [n]
  (when-let [w @world]
   (dotimes [_ n]
     (add-turtle! w (t/make-turtle (rand-coord w))))))

(defn scale-color
  [minv maxv v]
  (let [x0 minv x1 maxv
        y0 0    y1 255]
    (min 255
         (+ y0 (* (- v x0)
                  (/ (- y1 y0)
                     (- x1 x0)))))))

(defn follow-gradient
  [world turtle k minv]
  (let [mvmts [(forward turtle 1 world)
               (forward (right turtle 45) 1 world)
               (forward (left turtle 45) 1 world)]
        pherfn (comp k (partial patch-at world) coord)
        sorted (into (sorted-map) (zipmap (map pherfn mvmts) mvmts))
        best-move (last sorted)]
    (if (< minv (key best-move))
      (val best-move)
      turtle)))

(defn- activate-daemon
  [fns & [ctx]]
  (when-let [w @world]
    (let [txfns (map (fn [f]
                       (fn [ctx] (dosync (f ctx)))) fns)]
     (swap! daemons conj (d/activate-daemon (merge ctx {:world w}) txfns)))
    true))

(defn deactivate-all-daemons
  []
  (doseq [d @daemons]
    (d/deactivate-daemon d)
    (swap! daemons disj d)))

(defn activate-patch-daemon
  [fns]
  (doseq [p (patch-seq @world)]
   (activate-daemon fns {:patch-coord (coord p)})))

(defn activate-turtle-daemon
  [fns]
  (when-let [w @world]
    (doseq [t (turtles w)]
      (activate-daemon fns {:turtle-id (id t)}))))

(defn random-color-daemon
  [{:keys [world patch-coord]}]
  (let [patch (patch-at world patch-coord)
        color [(rand-int 255) (rand-int 255) (rand-int 255)]]
    (update-patch! world patch #(assoc % :color color))))

(defn random-turtle-color-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))
        color [(rand-int 255) (rand-int 255) (rand-int 255)]]
    (update-turtle! world t #(assoc % :color color))))

(defn walk-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))]
    (update-turtle! world t #(forward % 1 world))))

(defn wiggle-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))]
    (update-turtle! world t #(right % (- (rand-int 10) 5)))))

(defn sniff-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))]
   (update-turtle! world t #(follow-gradient world % :pheremone 0.1))))

(defn drop-pheremone-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))
        p (patch-at world (coord t))]
    (update-patch! world p #(update % :pheremone (fnil inc 0)))))

(def debugging (atom nil))
(defn diffusion-daemon
  [{:keys [world patch-coord]}]
  (let [patch (patch-at world patch-coord)
        nbrs (map (partial patch-at world) (neighbors world patch-coord))
        retain-pct 0.95
        pher-src (get patch :pheremone 0)
        src-share (* pher-src retain-pct)
        nbr-share (* pher-src (/ (- 1 retain-pct) (count nbrs)))]
    (update-patch! world patch #(assoc % :pheremone src-share))
    (for [nbr nbrs]
      (update-patch! world nbr #(update % :pheremone + nbr-share)))))

(defn evaporation-daemon
  [{:keys [world patch-coord]}]
  (let [patch (patch-at world patch-coord)]
   (update-patch! world patch #(update % :pheremone (fnil (partial * 0.9) 0)))))

(defn display-pheremone-daemon
  [{:keys [world patch-coord]}]
  (let [patch (patch-at world patch-coord)
        pher (get patch :pheremone 0)
        green (scale-color 0 3 pher)]
    (update-patch! world patch #(assoc % :color [0 green 0]))))

(comment
  (do (deactivate-all-daemons)
      (start-new-world 50 100)
      (create-turtles 5)
      (activate-patch-daemon [#_evaporation-daemon display-pheremone-daemon diffusion-daemon])
      (activate-turtle-daemon [walk-daemon wiggle-daemon drop-pheremone-daemon sniff-daemon]))

  )
