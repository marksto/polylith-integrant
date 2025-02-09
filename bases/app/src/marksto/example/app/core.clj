(ns marksto.example.app.core
  "Base API of an example app"
  (:require [clojure.tools.logging :as log]
            [marksto.example.app.config :as config]
            [marksto.example.app.system.core :as system])
  (:gen-class))

;; NB: Properly shutting production systems down is full of its own shenanigans,
;;     so we don't do much here. For more information, see the article "Killing
;;     me softly: Graceful shutdowns in Clojure".

(defn shutdown! []
  (system/halt!)
  (shutdown-agents))

(defn- add-shutdown-hooks! []
  (Runtime/.addShutdownHook (Runtime/getRuntime) (Thread. ^Runnable shutdown!)))

(defn -main [& _]
  ;; NB: Having the top-level `try-catch` block caters for both popular ways of
  ;;     launching the app — via 'clojure.main -m' and via vanilla '-jar' calls.
  (try
    (add-shutdown-hooks!)
    (system/init! (config/load!))
    (catch Throwable t
      (log/error t "Failed to launch the app system")
      (System/exit 1))))
