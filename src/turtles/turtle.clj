(ns turtles.turtle
  (:require [turtles.protocols :as proto]
            [turtles.world :refer [deg->unit-dir]]
            [turtles.math :refer [coord+ coord*]]))

(defrecord Turtle [id coord heading]
  proto/IIdentifiable
  (id [_] id)

  proto/IPositioned
  (coord [_] coord)

  proto/IColored
  (color [t] (get t :color [255 255 255]))

  proto/IMobile
  (heading
    [t]
    heading)
  (forward
    [t n sys]
    (let [offset (deg->unit-dir sys heading)]
      (update t :coord #(proto/wrap sys (coord* (coord+ % offset) n)))))
  (right
    [t n]
    (update t :heading #(mod (+ % n) 360))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constructors

(defn make-turtle
  [coord & [attrs]]
  (-> (or attrs {})
      (merge {:id (java.util.UUID/randomUUID)
              :coord coord
              :heading (rand-int 360)})
      map->Turtle))
