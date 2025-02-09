(ns marksto.example.config.interface
  "An abstract system configuration as a Polylith component"
  (:require [marksto.example.config.impl :as impl]))

(defn load!
  "Loads config from a given EDN `file`, narrowing it to a specific `profile`."
  [file profile]
  (impl/load! file profile))
