(ns turtles.patch
  "A patch:
   - Is a clojure ref allowing for coordinated, concurrent updates
   - Contains a set of turtles
   - Contains arbitrary key-value pairs for domain-specific data (e.g. pheremones)
   - Is positioned at a location"
  (:require [turtles.protocols :as proto]))

(defrecord BasicPatch [coord turtles]
  proto/IIdentifiable
  (id [_] coord)

  proto/IPositioned
  (coord [_] coord)

  proto/IInhabited
  (turtles
    [p]
    (seq turtles))
  (add-turtle
    [p t]
    (update p :turtles conj t))
  (remove-turtle
    [p t]
    (update p :turtles disj t))

  proto/IAttributed
  (get-attr [p a] (get p a))
  (set-attr [p a v] (assoc p a v))
  (unset-attr [p a] (dissoc p a))
  (update-attr [p a f] (update p a f)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constructors
;; - Default patch implementation chosen here

(defn make-patch
  [coord & [attrs]]
  (-> (or attrs {})
      (merge {:coord coord
              :turtles #{}})
      map->BasicPatch
      ref))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mutation API

(defn add-turtle!
  [p-ref t]
  (dosync
   (alter p-ref proto/add-turtle t)))

(defn remove-turtle!
  [p-ref t]
  (dosync
   (alter p-ref proto/remove-turtle t)))

(defn get-attr
  [p-ref a]
  (proto/get-attr @p-ref a))

(defn set-attr!
  [p-ref a v]
  (dosync
   (alter p-ref proto/set-attr a v)))

(defn unset-attr!
  [p-ref a]
  (dosync
   (alter p-ref proto/unset-attr a)))

(defn update-attr!
  [p-ref a f]
  (dosync
   (alter p-ref proto/update-attr a f)))
