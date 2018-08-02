(ns turtles.turtle
  (:require [turtles.protocols :as proto]))

(defrecord Turtle [id coord]
  proto/IIdentifiable
  (id [_] id)

  proto/IPositioned
  (coord [_] coord)

  proto/IColored
  (color [t] (get t :color [255 255 255])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constructors

(defn make-turtle
  [coord & [attrs]]
  (-> (or attrs {})
      (merge {:id (java.util.UUID/randomUUID)
              :coord coord})
      map->Turtle))
