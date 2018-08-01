(ns turtles.core
  (:require [quil.core :as q]
            [turtles.patch :as p :refer [get-attr set-attr! unset-attr! update-attr!]]
            [turtles.world :as w :refer [neighbors]]
            [turtles.world.rectangular :refer [make-rectangular-world]]
            [turtles.protocols
             :refer [patch-at coord limits wrap]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UI

(def px-scale 3)

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
   :draw #(w/draw-world world px-scale)
   :size (map #(* px-scale %)
              (w/bounds world))))
