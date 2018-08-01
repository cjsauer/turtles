(ns turtles.protocols)

(defprotocol IFinite
  "Finite object along arbitrary dimensions."
  (limits [x] "Returns a tuple of minimum and maximum limits along dimensions of x, e.g.
               [[minx miny minz ...] [maxx maxy maxz ...]]"))

(defprotocol IWrapped
  "Object forming a closed surface."
  (wrap [x coord] "Clamps the given coord onto the surface of x."))

(defprotocol ICoordinateSystem
  "A system of discrete coordinate mathematics."
  (distance [sys coord1 coord2] "Returns the distance between coord1 and coord2.")
  (unit-dirs [sys] "Returns a vector of coords representing the valid movement offsets."))

(defprotocol IPatchMatrix
  "coord->patch map"
  (patch-at [m coord] "Retrieves value stored in patch matrix p at coord.")
  (patch-seq [m] "Returns a consistent seq of all patches in matrix m."))

(defprotocol IPositioned
  "Situated in space."
  (coord [o] "Returns the position of o as a coord."))

(defn xpos [o] (first (coord o)))
(defn ypos [o] (second (coord o)))

(defprotocol IIdentifiable
  "Uniquely described by some value."
  (id [x] "Returns the unique id of x."))

(defprotocol IInhabited
  "Populated by turtles."
  (turtles [w] "Returns a seq of all turtles inhabiting w.")
  (add-turtle [w t] "Adds turtle t to inhabitants of w.")
  (remove-turtle [w t] "Removes turtle t from inhabitants of w."))

(defprotocol IAttributed
  "Describable with arbitrary key-value pairs."
  (get-attr [m a] "Returns the value mapped to a, or nil if not found.")
  (set-attr [m a v] "Sets attribute a to value v.")
  (unset-attr [m a] "Removes attribute a from m.")
  (update-attr [m a f] "Updates the value associated with a in m by applying f to its current value."))

(defprotocol IPatchArtist
  "Renderer of patches."
  (draw-patch [artist p scale] "Draws patch p to the screen at given scale."))
