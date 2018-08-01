(ns turtles.protocols)

(defprotocol IFinite
  "Finite object along arbitrary dimensions."
  (limits [x] "Returns a tuple of minimum and maximum limits along dimensions of x, e.g.
               [[minx miny minz ...] [maxx maxy maxz ...]]"))

(defprotocol IWrapped
  "Object forming a closed surface."
  (wrap [x coord] "Clamps the given coord onto the surface of x."))

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
