(ns marksto.example.app.system.embedded-pg
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [marksto.example.embedded-pg.interface :as embedded-pg]))

(defmethod ig/init-key :marksto.example.app.system/embedded-pg
  [_ pg-config]
  (log/info "Starting embedded PostgreSQL with config:" pg-config)
  ;; NB: Integrant itself doesn't have a safety net in case an exception occurs
  ;;     during components initialization. To avoid partially initialized state,
  ;;     it is recommended to manually wrap everything in `try-catch` blocks.
  (try
    (embedded-pg/start-postgres! pg-config)
    (catch Exception e
      (log/error e "Starting embedded PostgreSQL failed"))))

(defmethod ig/halt-key! :marksto.example.app.system/embedded-pg
  [_ embedded-pg]
  (log/info "Stopping embedded PostgreSQL")
  (try
    (embedded-pg/stop-postgres! embedded-pg)
    (catch Exception e
      (log/error e "Stopping embedded PostgreSQL failed"))))
