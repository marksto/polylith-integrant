(ns marksto.example.timepiece.impl
  (:require [java-time.api :as jt])
  (:import (java.time Clock LocalDateTime)))

(defn now
  ^LocalDateTime [^Clock clock]
  (jt/local-date-time clock))
