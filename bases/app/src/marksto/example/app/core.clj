(ns marksto.example.app.core
  "Base API of an example app"
  (:require [clojure.tools.logging :as log]
            [marksto.example.app.config :as config]
            [marksto.example.app.system.core :as system]
            [marksto.example.pg-ops.interface :as pg-ops])
  (:gen-class))

;; NB: Properly shutting production systems down is full of its own shenanigans,
;;     so we don't do much here. For more information, see the article "Killing
;;     me softly: Graceful shutdowns in Clojure".

(defn shutdown!
  [ig-system]
  (system/halt! ig-system)
  (shutdown-agents))

(defn- add-shutdown-hooks!
  [ig-system]
  (Runtime/.addShutdownHook
    (Runtime/getRuntime)
    (Thread. ^Runnable #(shutdown! ig-system))))

(defn launch! []
  (let [ig-config (config/load!)
        ig-system (system/init! ig-config)]
    (add-shutdown-hooks! ig-system)
    ig-system))

;;

;; NB: To keep this example app minimalistic we just query the Postgres version.
(defn do-something!
  [{:marksto.example.app.system/keys [data-source] :as _ig-system}]
  (let [pg-version (pg-ops/query-version data-source)]
    (log/info (format "The DBMS version: %s" pg-version))))

;;

(defn -main [& _]
  ;; NB: Having the top-level `try-catch` block caters for both popular ways of
  ;;     launching the app — via 'clojure.main -m' and via vanilla '-jar' calls.
  (try
    (do-something! (launch!))
    (catch Throwable t
      (log/error t "Failed to launch the app system")
      (System/exit 1))))
