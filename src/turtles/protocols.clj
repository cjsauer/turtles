(ns turtles.protocols)

;; A world is a finite play area that bounds simulations.
(defprotocol IWorld
  (limits [w] "Returns the coordinate extremes of world w as [minx maxx miny maxy],
               where minx and miny are inclusive, and maxx and maxy are exclusive.")
  (wrap [w coord] "Wraps the given coord back into the legal limits of world w."))

;; A system of discrete coordinate mathematics.
(defprotocol ICoordinateSystem
  (distance [sys coord1 coord2] "Returns the distance between coord1 and coord2.")
  (unit-dirs [sys] "Returns a vector of coords representing the valid movement offsets."))

;; coord->patch map
(defprotocol IPatchMatrix
  (patch-at [m coord] "Retrieves value stored in coordinate index ci at coord.")
  (patch-seq [m] "Returns a consistent seq of all patches in matrix m."))

;; Situated in space.
(defprotocol IPositioned
  (coord [o] "Returns the position of o as a coord."))

(defn xpos [o] (first (coord o)))
(defn ypos [o] (second (coord o)))

;; Discrete "patches" of spacetime, where turtles and "things" exist.
(defprotocol IPatch
  (conj-turtle [p t] "Conj turtle t into patch p.")
  (disj-turtle [p t] "Disj turtle t from patch p.")
  (get-attr [p a] "Returns value of attribute a for patch p.")
  (set-attr [p a v] "Sets attribute a equal to value v for patch p.")
  (unset-attr [p a] "Removes attribute a from patch p.")
  (update-attr [p a f] "Updates attribute a of patch p by applying function f to its value."))

;; Renderer of patches.
(defprotocol IPatchArtist
  (draw-patch [artist p scale] "Draws patch p to the screen at given scale."))
