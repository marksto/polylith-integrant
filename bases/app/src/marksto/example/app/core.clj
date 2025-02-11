(ns marksto.example.app.core
  "Base API of an example app"
  (:require [clojure.tools.logging :as log]
            [marksto.example.app.config :as config]
            [marksto.example.app.system.core :as system]
            [marksto.example.logic.interface :as logic])
  (:gen-class))

;; NB: Properly shutting production systems down is full of its own shenanigans,
;;     so we don't do much here. For more information, see the article "Killing
;;     me softly: Graceful shutdowns in Clojure".

(defn shutdown!
  [ig-system shutdown-agents?]
  (system/halt! ig-system)
  (when shutdown-agents?
    (shutdown-agents)))

(defn- add-shutdown-hooks!
  [ig-system]
  (Runtime/.addShutdownHook
    (Runtime/getRuntime)
    (Thread. ^Runnable #(shutdown! ig-system true))))

(defn launch!
  [ig-config]
  (let [ig-system (system/init! ig-config)]
    (add-shutdown-hooks! ig-system)
    ig-system))

;;

(defn build-app-ctx
  [{:marksto.example.app.system/keys [db] :as _ig-system}]
  ;; NB: This is the place where one can be creative. All required system state
  ;;     and/or its derivatives can be "injected" here into the app context map
  ;;     to be passed on to the application logic component in a myriad of ways.
  ;;     In a typical web application, the resulting map is then usually merged
  ;;     into an incoming request via a dedicated middleware.
  {:db db})

(defn run-app-logic!
  [ig-system]
  (let [app-ctx (build-app-ctx ig-system)
        app-res (logic/do-something! app-ctx)]
    (log/info (format "The DBMS version: %s" (:pg-version app-res)))
    app-res))

;;

(defn -main [& _]
  ;; NB: Having the top-level `try-catch` block caters for both popular ways of
  ;;     launching the app — via 'clojure.main -m' and via vanilla '-jar' calls.
  (try
    (-> (config/load!)
        (launch!)
        (run-app-logic!))
    (catch Throwable t
      (log/error t "Failed to launch the app system")
      (System/exit 1))))
