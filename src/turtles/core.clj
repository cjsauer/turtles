(ns turtles.core
  (:require [turtles.ui :as ui]
            [turtles.patch :refer [set-attr!]]
            [turtles.protocols :refer [patch-seq coord patch-at]]
            [turtles.world :as w]
            [turtles.world.rectangular :as wrect]
            [turtles.daemons :as d]))

(defonce world (atom nil))

(defn start-new-world
  [sizex sizey]
  (reset! world (wrect/make-rectangular-world sizex sizey))
  (ui/start-sketch @world))

(def daemons (atom #{}))

(defn activate-deamon
  [fns]
  (when @world
    (swap! daemons conj
           (d/activate-daemon @world nil nil fns))
    true))

(defn deactivate-all-daemons
  []
  (doseq [d @daemons]
    (d/deactivate-daemon d)
    (swap! daemons disj d)))

(defn random-color-deamon
  [world _ _]
  (let [[maxx maxy] (w/bounds world)
        p (patch-at world [(rand-int maxx) (rand-int maxy)])
        color [(rand-int 255) (rand-int 255) (rand-int 255)]]
    (set-attr! p :color color)))
