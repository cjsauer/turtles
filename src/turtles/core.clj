(ns turtles.core
  (:require [turtles.ui :as ui]
            [turtles.turtle :as t]
            [turtles.world :as w]
            [turtles.world.rectangular :as wrect]
            [turtles.daemons :as d]
            [turtles.protocols
             :as proto
             :refer [id
                     color
                     coord
                     heading
                     patch-at
                     update-turtle!
                     update-patch!
                     add-turtle!
                     select-turtles
                     turtles
                     forward
                     backward
                     right
                     left]]))

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

(defn- activate-daemon
  [fns & [ctx]]
  (when-let [w @world]
    (doseq [f fns]
      (swap! daemons conj
             (d/activate-daemon (merge ctx {:world w}) f)))
    true))

(defn deactivate-all-daemons
  []
  (doseq [d @daemons]
    (d/deactivate-daemon d)
    (swap! daemons disj d)))

(defn activate-patch-daemon
  [fns]
  (activate-daemon
   (map (fn [f]
          (fn [{:keys [world] :as ctx}]
            (let [p (patch-at world (rand-coord world))]
              (f (assoc ctx :patch p)))))
        fns)))

(defn activate-turtle-daemon
  [fns]
  (when-let [w @world]
    (doseq [t (turtles w)]
      (activate-daemon fns {:turtle-id (id t)}))))

(defn random-color-daemon
  [{:keys [world patch]}]
  (let [color [(rand-int 255) (rand-int 255) (rand-int 255)]]
    (update-patch! world patch #(assoc % :color color))))

(defn random-turtle-color-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))
        color [(rand-int 255) (rand-int 255) (rand-int 255)]]
    (update-turtle! world t #(assoc % :color color))))

(defn walk-forward-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))]
    (update-turtle! world t #(forward % 1 world))))

(defn wiggle-daemon
  [{:keys [world turtle-id]}]
  (let [t (first (select-turtles world #(= turtle-id (id %))))]
    (update-turtle! world t #(right % 10))))
