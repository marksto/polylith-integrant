(ns marksto.example.timepiece.interface
  (:require [marksto.example.timepiece.impl :as impl])
  (:import (java.time Clock LocalDateTime)))

(defn now
  "Retrieves the current timestamp using a given `clock`."
  ^LocalDateTime [^Clock clock]
  (impl/now clock))
