(ns turtles.patch
  (:require [turtles.protocols :as proto]
            [clojure.set :as set]))

(defrecord Patch [coord turtles]
  proto/IIdentifiable
  (id [_] coord)

  proto/IPositioned
  (coord [_] coord)

  proto/IColored
  (color [p] (get p :color [0 0 0])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constructors
;; - Default patch implementation chosen here

(defn make-patch
  [coord & [attrs]]
  (-> (or attrs {})
      (merge {:coord coord
              :turtles #{}})
      map->Patch))
