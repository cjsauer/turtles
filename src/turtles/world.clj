(ns turtles.world
  "A world is a collection of patches indexed by a finite coordinate system."
  (:require [turtles.math :as math]
            [turtles.protocols
             :as
             proto
             :refer
             [draw-patch limits patch-seq unit-dirs wrap]]))

(defn bounds
  "Returns the bounding box of world w as [width height]."
  [w]
  (let [[mins maxs] (limits w)]
    (map - maxs mins)))

(defn draw-world
  [w scale]
  (doseq [p (patch-seq w)]
    (draw-patch w p scale)))

(defn neighbors
  "Returns the neighbors of coord as a sequence of coords."
  [w coord]
  (map (comp (partial wrap w) math/coord+)
       (unit-dirs w)
       (repeat coord)))

(defn deg->unit-dir
  "Computes the unit-dir closest to the given heading in degrees."
  [w deg]
  (nth (unit-dirs w) (quot deg 360)))
