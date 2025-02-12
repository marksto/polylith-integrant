(ns marksto.example.supplier.interface
  (:require [marksto.example.supplier.impl :as impl]))

(def supported-things
  "A set of all things supported by supplier."
  impl/supported-things-set)

(defn rand-thing
  "Picks and returns a random thing."
  []
  (impl/rand-thing))

(defn supply-one
  "Returns just one `thing`."
  [thing]
  (impl/supply-one thing))

(defn supply-many
  "Returns many `thing`s at once in a vector.

   Accepts an optional `qty` param to supply a specific number of things.
   If omitted, will supply up to 10 things."
  ([thing]
   (impl/supply-many thing nil))
  ([thing qty]
   (impl/supply-many thing qty)))
