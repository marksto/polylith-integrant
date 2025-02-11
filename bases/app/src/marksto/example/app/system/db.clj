(ns marksto.example.app.system.db
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as jdbc.conn])
  (:import (com.zaxxer.hikari HikariDataSource)
           (java.sql Connection)))

(defn -conn-pool
  [{:keys [jdbc-url cp-opts] :as _ds-config}]
  (let [conn-pool (jdbc.conn/->pool HikariDataSource
                                    (merge cp-opts {:jdbcUrl jdbc-url}))]
    ;; NB: Initializing the `conn-pool` and performing a validation check.
    (Connection/.close (jdbc/get-connection conn-pool))
    conn-pool))

(defmethod ig/init-key :marksto.example.app.system/db
  [_ ds-config]
  (log/info "Making JDBC DataSource")
  ;; NB: Integrant itself doesn't have a safety net in case an exception occurs
  ;;     during components initialization. To avoid partially initialized state,
  ;;     it is recommended to manually wrap everything in `try-catch` blocks.
  (try
    ;; NB: It wraps the `DataSource` object into a so-called 'db-spec' map used
    ;;     for getting connections. This isn't required by the `next.jdbc`, but
    ;;     this may be necessary for other `clojure.java.jdbc`-based tools.
    {:datasource (-conn-pool ds-config)}
    (catch Exception e
      (log/error e "Making JDBC DataSource failed"))))

(defmethod ig/halt-key! :marksto.example.app.system/db
  [_ {:keys [datasource]}]
  (log/info "Closing JDBC DataSource")
  (try
    (HikariDataSource/.close datasource)
    (catch Exception e
      (log/error e "Closing JDBC DataSource failed"))))
