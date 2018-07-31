(ns turtles.protocols)

;; A world is a finite play area that bounds simulations.
(defprotocol IWorld
  (limits [w] "Returns the coordinate extremes of world w as [minx maxx miny maxy],
               where minx and miny are inclusive, and maxx and maxy are exclusive.")
  (wrap [w coord] "Wraps the given coord back into the legal limits of world w.")
  (patch-seq [w] "Returns a consistent seq of all patches in world w."))

(defprotocol IPatchArtist
  (draw-patch [this p scale] "Draws patch p to the screen at given scale."))

(defprotocol ICoordIndex
  (location [ci coord] "Retrieves value stored in coordinate index ci at coord."))

(defprotocol IPositioned
  (coord [o] "Returns the 2D coordinate position of o."))

(defn xpos [o] (first (coord o)))
(defn ypos [o] (second (coord o)))

(defprotocol IPatch
  (conj-turtle [p t] "Conj turtle t into patch p.")
  (disj-turtle [p t] "Disj turtle t from patch p.")
  (set-attr [p a v] "Sets attribute a equal to value v for patch p.")
  (unset-attr [p a] "Removes attribute a from patch p.")
  (update-attr [p a f] "Updates attribute a of patch p by applying function f to its value."))
