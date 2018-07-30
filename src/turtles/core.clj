(ns turtles.core
  (:require [quil.core :as q]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Util
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def sizex 128)
(def sizey 128)

(defn wrap
  "Wraps x between 0 and (dec limit)."
  [x limit]
  (mod x limit))

(defn wrap-coord
  [[x y] [limitx limity]]
  [(wrap x limitx)
   (wrap y limity)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Patches
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; The World is a matrix of Patch refs
(defonce world [])

(def ^:dynamic *patch*)

(defn- make-patch
  "Returns a map representing a patch at coordinate [x y]."
  [x y]
  {:class :patch
   :coord [x y]
   :patch-color [0 0 0]
   :creatures #{}
   :deamons []})

(defn clear-all
  "Resets the world back to a blank slate."
  []
  (alter-var-root #'world
                  (fn [_]
                    (vec (for [x (range sizex)]
                           (vec (for [y (range sizey)]
                                  (ref (make-patch x y))))))))
  true)

(defn- all-patch-refs
  "Returns a seq of all patch refs."
  []
  (flatten world))

(defn all-patches
  "Returns a consistent view of all patches in the world as a seq."
  []
  (dosync
   (doall (map deref (all-patch-refs)))))

(defn- patch-ref-at
  "Retrieves the patch ref at the given coordinate."
  [[x y :as coord]]
  (get-in world coord))

(defn patch-at
  "Retrieves the patch at the given coordinate."
  [[x y :as coord]]
  @(patch-ref-at coord))

(defn set-patch-color
  "Sets the color of the current patch."
  [color]
  (when *patch*
    (alter *patch* assoc :patch-color color)))

(defn distance
  "Calculates the distance from the current entity to the given coordinate."
  [[x y]]
  (when *patch*
    (let [[x2 y2] (:coord @*patch*)]
     (Math/sqrt (+ (Math/pow (- x x2) 2)
                   (Math/pow (- y y2) 2))))))

(defn xpos
  "Returns the x-coordinate of the current entity."
  []
  (when *patch*
    (-> @*patch* :coord first)))

(defn ypos
  "Returns the y-coordinate of the current entity."
  []
  (when *patch*
    (-> @*patch* :coord second)))

(defn patch-test
  "Runs f (assumed to be a side-effecting function) for every patch that satisfies
  pred."
  [pred f]
  (dosync
   (dorun
    (map #(binding [*patch* %]
            (when (pred) (f)))
         (all-patch-refs)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Turtles
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce next-creature-id (atom 0))

(def ^:dynamic *turtle*)

(defn make-turtle
  "Returns a map representing a turtle at coordinate coord."
  [[x y :as coord] heading]
  {:id (swap! next-creature-id inc)
   :class :turtle
   :coord (wrap-coord coord [sizex sizey])
   :heading (wrap heading 8)
   :color [255 255 255]
   :daemons []})

(defn create-turtles
  "Creates n turtles placed randomly around the world."
  [n]
  (dosync
   (dotimes [i n]
     (let [coord [(rand-int sizex) (rand-int sizey)]
           heading (rand-int 8)
           patch (patch-ref-at coord)
           turtle (make-turtle coord heading)]
       (alter patch update :creatures conj turtle)))))

(defn is-turtle?
  "Returns true if entity e is a turtle, false otherwise."
  [e]
  (= (:class e) :turtle))

(defn all-turtles
  "Returns a consistent view of all turtles in the world as a seq."
  []
  (dosync
   (into [] (comp (mapcat :creatures)
                  (filter is-turtle?))
         (all-patches))))

(defn turtles-here
  "Returns set of turtles occupying current patch."
  []
  (when *patch*
    (->> *patch*
         deref
         :creatures
         (filter is-turtle?)
         (into #{}))))

(defn- update-patch-creature
  [old-creature new-creature]
  (when *patch*
    (dosync
     (alter *patch* update :creatures disj old-creature)
     (alter *patch* update :creatures conj new-creature))))

(defn set-color
  "Sets the color of the current turtle."
  [color]
  (when *turtle*
    (update-patch-creature *turtle* (assoc *turtle* :color color))))

(def dir->delta
  {0 [0 -1]
   1 [1 -1]
   2 [1 0]
   3 [1 1]
   4 [0 1]
   5 [-1 1]
   6 [-1 0]
   7 [-1 -1]})

(defn vec+
  [v1 v2]
  (mapv + v1 v2))

(defn vec*
  [s v1]
  (mapv #(* s %) v1))

(defn forward
  "Steps the current turtle forward by n units in the direction of its heading."
  [n]
  (when *turtle*
    (let [offset (->> *turtle* :heading dir->delta (vec* n))
          new-turtle (update *turtle* :coord #(wrap-coord (vec+ % offset)
                                                          [sizex sizey]))]
      (update-patch-creature *turtle* new-turtle))))

(defn backward
  "Steps the current turtle backward by n units in the opposite direction of the heading."
  [n]
  (forward (- n)))

(defn right
  "Turns the current turtle to the right by n degrees."
  [n]
  (when *turtle*
    (let [offset (quot n 45)
          new-turtle (update *turtle* :heading #(wrap (+ % offset) 8))]
      (update-patch-creature *turtle* new-turtle))))

(defn left
  "Turns the current turtle to the left by n degrees."
  [n]
  (right (- n)))

(defn turtle-test
  "Runs f (assumed to be a side-effecting function) on every turtle that satisfies
  pred."
  [pred f]
  (patch-test
   #(< 0 (count (turtles-here)))
   (fn []
     (dorun
      (map #(binding [*turtle* %]
              (when (pred) (f)))
           (turtles-here))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Daemons
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; UI
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-ui
  []
  (q/frame-rate 1)
  (q/background 0))

(def patch-px-scale 5)

(defn clear-screen
  []
  (q/fill 0)
  (q/rect 0 0 sizex sizey))

(defn draw-rect
  [x y]
  (q/rect (* x patch-px-scale)
          (* y patch-px-scale)
          patch-px-scale
          patch-px-scale))

(defn draw-patch
  [{[x y] :coord
    [r g b] :patch-color
    :as patch}]
  (q/fill r g b)
  (draw-rect x y))

(defn draw-creature
  [{[x y] :coord
    [r g b] :color
    :as creature}]
  (q/fill r g b)
  (draw-rect x y))

(defn draw-ui
  []
  (let [ps (all-patches)
        creatures (mapcat :creatures ps)]
    (clear-screen)
    (doseq [patch ps]
      (draw-patch patch))
    (doseq [c creatures]
      (draw-creature c))))

(comment
 (q/defsketch ui
   :title "Turtles"
   :settings #(q/smooth 2)
   :setup setup-ui
   :draw draw-ui
   :size [(* sizex patch-px-scale)
          (* sizey patch-px-scale)])

 )
