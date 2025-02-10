(ns marksto.example.app.system.core
  "Integrant system for the `app` base

   For our system we follow the Integrant conventions on loading namespaces
   that contain the system components (a.k.a. keys in Integrant's parlance)
   named with qualified keywords that match their namespaces. For instance,
   a 'Server' component residing in `marksto.example.app.system.server` ns
   obtains the `:marksto.example.app.system/server` key in the system map.

   This approach leverages the uniqueness of Polylith base names and allows
   us to use a particular Polylith component under classpath-unique keys as
   part of multiple Integrant systems/bases.

   Another option is to use an arbitrary key, e.g. `:app/config`, though it
   may lead to naming collisions and overwriting of multi-methods' dispatch
   values in case if you plan to run multiple Integrant systems together in
   a single workspace/project."
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [marksto.example.app.system.state :as state]))

;; NB: This ns exists solely for the purpose of demonstrative testing.
;;     It wraps Integrant core `init!`/`halt!` fns to store the state.
;;     Exposing an Integrant system state should generally be avoided.
;;     See the `user.clj` on how to deal with the system in REPL.

(defn halt!
  ([ig-system]
   (halt! ig-system (keys ig-system)))
  ([ig-system keys]
   (log/info "Halting the app system...")
   (when ig-system
     (ig/halt! ig-system keys)
     (log/info "System successfully halted")
     (state/swap-state! (constantly nil))
     nil)))

(defn init!
  ([ig-config]
   (init! ig-config (keys ig-config)))
  ([ig-config keys]
   (log/info "Initiating the app system...")
   (when ig-config
     (ig/load-namespaces ig-config)
     (log/info "Loaded namespaces")
     (let [ig-system (ig/init ig-config keys)]
       (log/info "System successfully initiated")
       (state/swap-state! (constantly
                            {:system ig-system
                             :config ig-config}))
       ig-system))))

(defn ^:tests-only get-state []
  @state/*state)
