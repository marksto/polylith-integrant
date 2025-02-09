(ns marksto.example.app.core
  (:require [clojure.pprint :as pp]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [marksto.example.app.state :as state]))

;; Integrant System

;; NB: For our system we follow the Integrant conventions on loading namespaces
;;     that contain the system components (a.k.a. keys in Integrant's parlance)
;;     named with qualified keywords that match their namespaces. For instance,
;;     a 'Config' component residing in `marksto.example.app.config` namespace
;;     obtains the `:marksto.example.app/config` key in the system map.
;;
;;     This approach leverages the uniqueness of Polylith base names and allows
;;     us to use a particular Polylith component under classpath-unique keys as
;;     part of multiple Integrant systems/bases.
;;
;;     Another option is to use an arbitrary key, e.g. `:app/config`, though it
;;     may lead to naming collisions and overwriting of multi-methods' dispatch
;;     values in case if you plan to run multiple Integrant systems together in
;;     a single workspace/project.

(def default-ig-config
  {:marksto.example.app/config      {}
   :marksto.example.app/embedded-pg {:config (ig/ref :marksto.example.app/config)
                                     #_#_:log-file "path/to/pg-logs-redirection"}
   :marksto.example.app/data-source {:config   (ig/ref :marksto.example.app/config)
                                     :postgres (ig/ref :marksto.example.app/embedded-pg)}})

(defn halt-system! []
  (state/stop!)
  ::stopped)

(defn init-system!
  ([]
   (init-system! default-ig-config))
  ([ig-config]
   (log/info "Starting system with config:\n"
             (with-out-str (pp/pprint ig-config)))
   (try
     (halt-system!)
     (state/start! ig-config)
     ::started
     (catch Exception ex
       (log/error ex "Failed to start the system")
       (halt-system!)
       ::failed-to-start))))

;; Base API

;; NB: Properly shutting production systems down is full of its own shenanigans,
;;     so we don't do much here. For more information, see the article "Killing
;;     me softly: Graceful shutdowns in Clojure".

(defn shutdown!
  []
  (let [halt-res (halt-system!)]
    (shutdown-agents)
    halt-res))

(defn launch!
  []
  (.addShutdownHook (Runtime/getRuntime) (Thread. shutdown!))
  (init-system!))

(defn -main
  [& _]
  (launch!))
