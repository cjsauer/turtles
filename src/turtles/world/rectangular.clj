(ns turtles.world.rectangular
  "2D Cartesian grid world implementation."
  (:require [quil.core :as q]
            [turtles.math :as math]
            [turtles.patch :as p]
            [turtles.protocols :as proto]
            [clojure.set :as set]))

(defrecord RectangularWorld [sizex sizey patches turtles]
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

  proto/IPatched
  (patch-at
    [w coord]
    @(get-in patches coord))
  (patch-seq
    [{:keys [patches]}]
    (dosync
     (doall
      (map deref (flatten patches)))))

  proto/IInhabited
  (turtles
    [w]
    (dosync
     (doall
      (map deref (vals @turtles)))))
  (select-turtles
    [w pred]
    (dosync
     (let [tset (into #{} (proto/turtles w))]
       (set/select pred tset))))

  proto/IWorld
  (update-patch!
    [w p f]
    (dosync
     (let [p* (get-in patches (proto/coord p))]
       (alter p* f))))
  (add-turtle!
    [w t]
    (swap! turtles assoc (proto/id t) (ref t)))
  (remove-turtle!
    [w t]
    (swap! turtles dissoc (proto/id t)))
  (update-turtle!
    [w t f]
    (dosync
     (when-let [turt (get @turtles (proto/id t))]
       (alter turt f))))

  proto/IPatchArtist
  (draw-patch
    [_ patch scale]
    (let [patch-color (proto/color patch)
          [x y] (proto/coord patch)]
      (apply q/fill patch-color)
      (q/rect (* scale x)
              (* scale y)
              scale
              scale)))

  proto/ITurtleArtist
  (draw-turtle
    [_ t scale]
    (let [turtle-color (proto/color t)
          [x y] (proto/coord t)]
      (apply q/fill turtle-color)
      (q/rect (* scale x)
              (* scale y)
              scale
              scale))))

(defn make-rectangular-world
  [sizex sizey]
  (let [patches (vec
                 (for [x (range sizex)]
                   (vec
                    (for [y (range sizey)]
                      (ref (p/make-patch [x y])
                           :min-history 100)))))]
    (map->RectangularWorld
     {:sizex sizex
      :sizey sizey
      :patches patches
      :turtles (atom {})})))
