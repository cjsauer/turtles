(ns turtles.world.square
  "2D Cartesian grid world implementation."
  (:require [quil.core :as q]
            [turtles.patch :as p]
            [turtles.protocols :as proto]))

(defrecord SquareWorld [sizex sizey patches]
  proto/IWorld
  (limits [w] [0 sizex 0 sizey])
  (wrap [w [x y]]
    [(mod x sizex) (mod y sizey)])
  (patch-seq
    [{:keys [patches]}]
    (dosync
     (doall
      (map deref (flatten patches)))))

  proto/ICoordIndex
  (location [w coord] (get-in patches coord))

  proto/IPatchArtist
  (draw-patch
    [_
     {:keys [color], :as p, :or {color [0 0 0]}}
     scale]
    (apply q/fill color)
    (q/rect (* scale (proto/xpos p))
            (* scale (proto/ypos p))
            scale
            scale)))

(defn make-world
  [sizex sizey]
  (let [patches (vec (for [x (range sizex)]
                       (vec (for [y (range sizey)]
                              (p/make-patch [x y])))))]
    (map->SquareWorld
     {:sizex sizex
      :sizey sizey
      :patches patches})))
