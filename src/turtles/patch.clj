(ns turtles.patch
  "A patch:
   - Is a clojure ref allowing for coordinated, concurrent updates
   - Contains a set of turtles
   - Contains arbitrary key-value pairs for domain-specific data (e.g. pheremones)
   - Is positioned at a location"
  (:require [turtles.protocols :as proto]
            [turtles.patch.hashmap :as impl]))

(def reserved-attributes #{:turtles :coord})

(defn- verify-attr!
  "Throws if attr is illegal or reserved. Returns nil otherwise."
  [attr]
  (when (contains? reserved-attributes attr)
    (throw (ex-info (format "Illegal patch attribute %s" attr)
                    {::illegal-attribute attr}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constructors
;; - Default patch implementation chosen here

(defn make-patch
  [coord & [attrs]]
  (-> (or attrs {})
      (merge {:coord coord
              :turtles #{}})
      impl/map->HashMapPatch
      ref))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mutation API

(defn conj-turtle!
  [p-ref t]
  (dosync
   (alter p-ref proto/conj-turtle t)))

(defn disj-turtle!
  [p-ref t]
  (dosync
   (alter p-ref proto/disj-turtle t)))

(defn get-attr
  [p-ref a]
  (proto/get-attr @p-ref a))

(defn set-attr!
  [p-ref a v]
  (verify-attr! a)
  (dosync
   (alter p-ref proto/set-attr a v)))

(defn unset-attr!
  [p-ref a]
  (verify-attr! a)
  (dosync
   (alter p-ref proto/unset-attr a)))

(defn update-attr!
  [p-ref a f]
  (verify-attr! a)
  (dosync
   (alter p-ref proto/update-attr a f)))
