(ns turtles.math)

(defn coord+
  "Adds two coordinates together."
  [coord1 coord2]
  (mapv + coord1 coord2))

(defn coord*
  "Scales coord by scalar s."
  [coord s]
  (mapv (partial * s) coord))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Standard library shims

(defn sqrt
  [x]
  (Math/sqrt x))

(defn pow
  [x p]
  (Math/pow x p))
