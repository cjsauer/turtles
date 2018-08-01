(ns turtles.patch.hashmap
  "Implementation of IPatch using a standard clojure hashmap and record."
  (:require [turtles.protocols :as proto]))

(defrecord HashMapPatch [coord turtles]
  proto/IPositioned
  (coord [_] coord)

  proto/IPatch
  (conj-turtle
    [p t]
    (update p :turtles conj t))
  (disj-turtle
    [p t]
    (update p :turtles disj t))
  (get-attr
    [p a]
    (get p a))
  (set-attr
    [p a v]
    (assoc p a v))
  (unset-attr
    [p a]
    (dissoc p a))
  (update-attr
    [p a f]
    (update p a f)))
