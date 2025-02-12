(ns user
  (:require [clojure.pprint :as pp]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [integrant.core :as ig]
            [integrant.repl :as ir]
            [integrant.repl.state :as ir.state]
            [marksto.example.app.config :as app-config]
            [marksto.example.app.logic.core :as app-logic]
            [next.jdbc.specs :as jdbc.specs]))

;;;; Global

(println "Loaded root `user.clj` ns\n")

;; NB: No need to refresh the `user.clj` ns, "scripts" or "projects".
(set-refresh-dirs "bases/app/src" "components/**/src")

;; NB: Provides explicit argument checking and better error messages
;;     for some common mistakes by instrumenting `next.jdbc` API fns.
(jdbc.specs/instrument)

(defn disable-jdbc-specs []
  (jdbc.specs/unstrument))


;;;; System

;; NB: We use the Integrant-REPL library to follow Reloaded Workflow,
;;     which is fine for our use case, since we have a single system,
;;     i.e. a single running base. It can be easily changed later on.
;;     See https://github.com/weavejester/integrant-repl for details.

(defn system []
  ir.state/system)

;;

(defn app-ig-config []
  (let [ig-config (app-config/load! :dev)]
    (log/info "Starting system with config:\n" (with-out-str (pp/pprint ig-config)))
    (ig/load-namespaces ig-config)
    ig-config))

(defn start-app-system! []
  (ir/set-prep! #(ig/expand (app-ig-config)))
  (ir/go))

(defn stop-app-system! []
  (ir/halt))

;;

(comment
  ;; 1. Starting a new system
  ;;    See the logs:
  ;;    - user:326 - Starting system with config: ...
  ;;    - db:326 - Making JDBC DataSource
  (start-app-system!) ;=> :initiated

  ;; 2. Getting the current system in full
  (system) ;=> #:marksto.example.app.system{:db ...}

  ;; 3. Getting a particular component (key)
  (:marksto.example.app.system/db (system)) ;=> {:datasource ...}

  ;; 4. Restarting the running system
  ;;    See the logs:
  ;;    - user:326 - Starting system with config: ...
  ;;    - db:326 - Closing JDBC DataSource
  ;;    - db:326 - Making JDBC DataSource
  (start-app-system!) ;=> :initiated

  ;; 5. Reloading source files and restarting the system in one go
  ;;    See the logs:
  ;;    - db:326 - Closing JDBC DataSource
  ;;    - :reloading (marksto.example.app.system.db marksto.example.app.config marksto.example.app.core)
  ;;    - user:326 - Starting system with config: ...
  ;;    - db:326 - Making JDBC DataSource
  (ir/reset) ;=> :resumed

  ;; 6. Stopping the running system
  ;;    See the logs:
  ;;    - db:326 - Closing JDBC DataSource
  (stop-app-system!) ;=> :halted
  .)


;;;; App Logic

(defn run-app-logic!
  [& [extra-ctx]]
  (app-logic/run-app! (system) extra-ctx))

(comment
  ;; Checking that our application logic works
  (run-app-logic!)
  (run-app-logic! {:supply (constantly "Hello!")})
  .)
