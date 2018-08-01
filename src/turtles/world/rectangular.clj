(ns turtles.world.rectangular
  "2D Cartesian grid world implementation."
  (:require [quil.core :as q]
            [turtles.math :as math]
            [turtles.patch :as p]
            [turtles.protocols :as proto :refer [xpos ypos get-attr]]))

(defrecord RectangularWorld [sizex sizey patches]
  proto/IFinite
  (limits
    [w]
    [[0 0] [sizex sizey]])

  proto/IWrapped
  (wrap
    [w [x y]]
    [(mod x sizex) (mod y sizey)])

  proto/ICoordinateSystem
  (unit-dirs
    [w]
    [[1 0]
     [1 1]
     [0 1]
     [-1 1]
     [-1 0]
     [-1 -1]
     [0 -1]
     [1 -1]])
  (distance
    [w [x1 y1] [x2 y2]]
    (math/sqrt
     (+ (math/pow (- x2 x1) 2)
        (math/pow (- y2 y1) 2))))

  proto/IPatchMatrix
  (patch-at
    [w coord]
    (get-in patches coord))
  (patch-seq
    [{:keys [patches]}]
    (dosync
     (doall
      (map deref (flatten patches)))))

  proto/IPatchArtist
  (draw-patch
    [_ patch scale]
    (let [patch-color (or (get-attr patch :color)
                          [0 0 0])]
      (apply q/fill patch-color)
      (q/rect (* scale (xpos patch))
              (* scale (ypos patch))
              scale
              scale))))

(defn make-rectangular-world
  [sizex sizey]
  (let [patches (vec (for [x (range sizex)]
                       (vec (for [y (range sizey)]
                              (p/make-patch [x y])))))]
    (map->RectangularWorld
     {:sizex sizex
      :sizey sizey
      :patches patches})))
