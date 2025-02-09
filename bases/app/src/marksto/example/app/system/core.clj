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

(defn halt! []
  (log/info "Halting the app system...")
  (state/swap-state! (fn [{:keys [system]}]
                       (when system (ig/halt! system))
                       nil))
  ::halted)

(defn init!
  ([ig-config]
   (init! ig-config (keys ig-config)))
  ([ig-config keys]
   (log/info "Initiating the app system...")
   (try
     (when ig-config
       (ig/load-namespaces ig-config)
       (log/info "Loaded namespaces")
       (state/swap-state! (fn [_]
                            {:system (ig/init ig-config keys)
                             :config ig-config})))
     ::initiated
     (catch Exception ex
       (halt!)
       (throw ex)))))

;; NB: This fn exists solely for the purpose of demonstrative testing.
;;     Exposing an Integrant system state should generally be avoided.
;;     See the `user.clj` on how to deal with the system in REPL.
(defn ^:tests-only get-state []
  @state/*state)
