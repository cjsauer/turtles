(ns turtles.core
  (:require [turtles.ui :as ui]
            [turtles.protocols :refer [patch-at update-patch!]]
            [turtles.world :as w]
            [turtles.world.rectangular :as wrect]
            [turtles.daemons :as d]))

(defonce world (atom nil))
(def daemons (atom #{}))

(defn start-new-world
  [sizex sizey]
  (reset! world (wrect/make-rectangular-world sizex sizey))
  (ui/start-sketch @world))

(defn- lift-daemon
  [f]
  (fn [ctx]
    (f ctx)
    ctx))

(defn activate-daemon
  [fns]
  (when @world
    (doseq [f fns]
      (swap! daemons conj
             (d/activate-daemon {:world @world} f)))
    true))

(defn deactivate-all-daemons
  []
  (doseq [d @daemons]
    (d/deactivate-daemon d)
    (swap! daemons disj d)))

(defn random-color-deamon
  [{:keys [world]}]
  (let [[maxx maxy] (w/bounds world)
        p (patch-at world [(rand-int maxx) (rand-int maxy)])
        color [(rand-int 255) (rand-int 255) (rand-int 255)]]
    (update-patch! world p #(assoc % :color color))))
