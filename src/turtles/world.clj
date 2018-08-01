(ns turtles.world
  "A world is a collection of patches indexed by a finite coordinate system."
  (:require [turtles.math :as math]
            [turtles.protocols :as proto
             :refer [limits wrap patch-seq get-at draw-patch unit-dirs]]
            [turtles.world.square :as sq]))

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

(defn neighbors
  "Returns the neighbors of coord as a sequence of coords."
  [w coord]
  (map (comp (partial proto/wrap w) math/coord+)
       (unit-dirs w)
       (repeat coord)))

(defn deg->unit-dir
  "Computes the unit-dir closest to the given heading in degrees."
  [w deg]
  (nth (unit-dirs w) (quot deg 360)))
