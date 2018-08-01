(ns turtles.core
  (:require [quil.core :as q]
            [turtles.config :as cfg]
            [turtles.patch :as p :refer [get-attr set-attr! unset-attr! update-attr!]]
            [turtles.world :as w :refer [make-square-world neighbors]]
            [turtles.protocols
             :refer [get-at coord]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UI

(defn- setup-sketch
  []
  (q/background 0)
  (q/frame-rate 1))

(defn start-sketch
  "Creates a new quil sketch of the given world."
  [world]
  (q/sketch
   :title "Turtles"
   :setup setup-sketch
   :draw #(w/draw-world world cfg/px-scale)
   :size (map #(* cfg/px-scale %)
              (w/bounds world))))
