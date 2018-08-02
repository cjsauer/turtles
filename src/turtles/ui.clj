(ns turtles.ui
  (:require [quil.core :as q]
            [turtles.world :as w]))

(def px-scale 5)

(defn- setup-sketch
  []
  (q/background 0)
  (q/frame-rate 30))

(defn start-sketch
  "Creates a new quil sketch of the given world."
  [world]
  (q/sketch
   :title "Turtles"
   :setup setup-sketch
   :draw #(w/draw-world world px-scale)
   :size (map #(* px-scale %)
              (w/bounds world))))
