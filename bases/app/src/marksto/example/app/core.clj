(ns marksto.example.app.core
  "Base API of the example application.

   For our system we follow the Integrant conventions on loading namespaces
   that contain the system components (a.k.a. keys in Integrant's parlance)
   named with qualified keywords that match their namespaces. For instance,
   a 'DB (DataSource)' component resides in `marksto.example.app.system.db`
   ns and obtains the `:marksto.example.app.system/db` key in a system map.

   This approach leverages the uniqueness of Polylith base names and allows
   us to use a particular Polylith component under classpath-unique keys as
   part of multiple Integrant systems/bases.

   Another option is to use an arbitrary key, e.g. `:app/config`, though it
   may lead to naming collisions and overwriting of multi-methods' dispatch
   values in case if you plan to run multiple Integrant systems together in
   a single workspace/project.

   See the `user.clj` on how to deal with the Integrant system(s) in REPL."
  (:require [clansi.core :refer [style]]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [marksto.example.app.config :as config]
            [marksto.example.logic.interface :as logic])
  (:gen-class))

;;; State Mgmt

;; NB: Properly shutting production systems down is full of its own shenanigans,
;;     so we don't do much here. For more information, see the article "Killing
;;     me softly: Graceful shutdowns in Clojure".

(defn shutdown!
  [ig-system shutdown-agents?]
  (log/info "Halting the app system...")

  (ig/halt! ig-system)
  (log/info "System successfully halted")

  (when shutdown-agents?
    (shutdown-agents)))

(defn- add-shutdown-hooks!
  [ig-system]
  (Runtime/.addShutdownHook
    (Runtime/getRuntime)
    (Thread. ^Runnable #(shutdown! ig-system true))))

(defn launch!
  [ig-config]
  (log/info "Launching the app system...")

  (ig/load-namespaces ig-config)
  (log/info "Loaded component namespaces")

  (let [ig-system (ig/init ig-config)]
    (log/info "System successfully initiated")

    (add-shutdown-hooks! ig-system)

    ig-system))


;;; App Logic

(defn print-results
  [{:keys [current-ts pg-version] :as _app-res}]
  (log/info (style (format "Current timestamp: %s" current-ts) :blue))
  (log/info (style (format "The DBMS version: %s" pg-version) :blue)))

(defn build-app-ctx
  [{:marksto.example.app.system/keys [db] :as _ig-system} extra-ctx]
  ;; NB: This is the place where one can be creative. All required system state
  ;;     and/or its derivatives can be "injected" here into the app context map
  ;;     to be passed on to the application logic component in a myriad of ways.
  ;;     In a typical web application, the resulting map is then usually merged
  ;;     into an incoming request via a dedicated middleware.
  (merge {:db db} extra-ctx))

(defn run-app-logic!
  ([ig-system]
   (run-app-logic! ig-system nil))
  ([ig-system extra-ctx]
   (let [app-ctx (build-app-ctx ig-system extra-ctx)
         app-res (logic/do-something! app-ctx)]
     (print-results app-res)
     app-res)))


;;; Entrypoint

(defn -main [& _]
  ;; NB: Having the top-level `try-catch` block caters for both popular ways of
  ;;     launching the app — via 'clojure.main -m' and via vanilla '-jar' calls.
  (try
    (-> (config/load!)
        (launch!)
        (run-app-logic!))
    (catch Throwable t
      (log/error t "The example application failed")
      (System/exit 1))))
