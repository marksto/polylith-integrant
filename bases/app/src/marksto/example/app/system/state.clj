(ns marksto.example.app.system.state
  "The state of the running Integrant system

   IMPORTANT:
   This namespace exists for demonstration purposes only. Normally, except for
   the development environment, there is no need to expose Integrant system or
   its config map, but in this example we do it to be able to test things out."
  (:refer-clojure :exclude [get])
  (:require [clojure.tools.namespace.repl :as repl]))

(repl/disable-reload!)

(def *state
  "Stores the current Integrant system and its config."
  (atom nil))

(defn swap-state!
  [new-state-fn]
  (swap! *state new-state-fn))
