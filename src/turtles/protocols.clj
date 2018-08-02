(ns turtles.protocols)

(defprotocol IFinite
  "Characterized by n finite dimensions."
  (limits [x] "Returns a tuple of minimum and maximum limits along dimensions of x, e.g.
               [[minx miny minz ...] [maxx maxy maxz ...]]"))

(defprotocol IWrapped
  "Object forming a closed surface."
  (wrap [x coord] "Clamps the given coord onto the surface of x."))

(defprotocol ICoordinateSystem
  "A system of discrete coordinate mathematics."
  (distance [sys coord1 coord2] "Returns the distance between coord1 and coord2.")
  (unit-dirs [sys] "Returns a vector of coords representing the valid movement offsets."))

(defprotocol IPatched
  "Broken up into patches along its dimensions."
  (patch-at [m coord] "Retrieves patch located at coordinate coord.")
  (patch-seq [m] "Returns a seq of all patches in m."))

(defprotocol IWorld
  "Mutable environment that is the setting for simulations."
  (update-patch! [w p f] "Mutates patch p in world w by applying it to function f."))

(defprotocol IPositioned
  "Situated in space."
  (coord [o] "Returns the position of o as a coord."))

(defprotocol IColored
  "Colorful."
  (color [o] "Returns the color of object o as [r g b]."))

(defprotocol IIdentifiable
  "Uniquely described by some value."
  (id [x] "Returns the unique id of x."))

(defprotocol IInhabited
  "Populated by turtles."
  (turtles [w] "Returns a seq of all turtles inhabiting w.")
  (add-turtle [w t] "Adds turtle t to inhabitants of w.")
  (remove-turtle [w t] "Removes turtle t from inhabitants of w.")
  (select-turtles [w pred] "Returns all turtles in w satisfying predicate pred."))

(defprotocol IPatchArtist
  "Renderer of patches."
  (draw-patch [artist p scale] "Draws patch p to the screen at given scale."))
