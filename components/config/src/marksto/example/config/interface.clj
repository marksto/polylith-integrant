(ns marksto.example.config.interface
  "An abstract system configuration as a Polylith component"
  (:require [marksto.example.config.core :as core]))

(defn load-config
  []
  (core/load-config))
