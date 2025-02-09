(ns marksto.example.app.system.embedded-pg
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [marksto.example.embedded-pg.interface :as embedded-pg]))

(defmethod ig/init-key :marksto.example.app.system/embedded-pg
  [_ pg-config]
  (log/info "Initializing Postgres with config:" pg-config)
  (embedded-pg/start-postgres! pg-config))

(defmethod ig/halt-key! :marksto.example.app.system/embedded-pg
  [_ embedded-pg]
  (when embedded-pg
    (log/info "Halting Postgres")
    (embedded-pg/stop-postgres! embedded-pg)
    nil))
