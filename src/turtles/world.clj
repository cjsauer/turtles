(ns turtles.world
  "A world is a collection of patches indexed by a finite coordinate system."
  (:require [turtles.protocols :as proto]
            [turtles.world.square :as sq]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Protocol functions

(def limits proto/limits)
(def wrap proto/wrap)
(def patch-seq proto/patch-seq)
(def location proto/location)
(def draw-patch proto/draw-patch)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constructors

(defn make-square-world
  [sizex sizey]
  (sq/make-world sizex sizey))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API

(defn bounds
  "Returns the bounding box of world w as [width height]."
  [w]
  (let [[minx maxx miny maxy] (limits w)]
    [(- maxx minx)
     (- maxy miny)]))

(defn draw-world
  [w scale]
  (doseq [p (patch-seq w)]
    (draw-patch w p scale)))
