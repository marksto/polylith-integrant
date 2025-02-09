(ns marksto.example.app.system.data_source
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig])
  (:import (com.zaxxer.hikari HikariDataSource)
           (java.sql Connection)))

(defn -conn-pool
  ^HikariDataSource
  [{:keys [classname subprotocol subname user password]
    :as   db-spec}]
  (log/debug "DB Spec:" db-spec)
  (let [conn-pool (doto (HikariDataSource.)
                    (HikariDataSource/.setDriverClassName classname)
                    (HikariDataSource/.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
                    (HikariDataSource/.setUsername user)
                    (HikariDataSource/.setPassword password))]
    ;; NB: Initializing the pool and performing a validation check.
    (Connection/.close (HikariDataSource/.getConnection conn-pool))
    conn-pool))

(defmethod ig/init-key :marksto.example.app.system/data-source
  [_ {:keys [port dbname user password]}]
  (log/info "Initializing JDBC DataSource")
  (let [db-spec {:classname   "org.postgresql.Driver"
                 :subprotocol "postgresql"
                 :subname     (format "//localhost:%s/%s" port dbname)
                 :user        (or user "postgres")
                 :password    (or password "postgres")}]
    {:datasource (-conn-pool db-spec)}))

(defmethod ig/halt-key! :marksto.example.app.system/data-source
  [_ {:keys [datasource]}]
  (log/info "Closing JDBC DataSource")
  (when datasource
    (HikariDataSource/.close datasource)))
