(ns marksto.example.embedded-pg.interface
  "An embedded PostgreSQL DBMS as a Polylith component"
  (:require [marksto.example.embedded-pg.core :as core])
  (:import
    (io.zonky.test.db.postgres.embedded EmbeddedPostgres)))

(defn start-postgres!
  [pg-config]
  (core/start-postgres! pg-config))

(defn stop-postgres!
  [^EmbeddedPostgres embedded-pg]
  (core/stop-postgres! embedded-pg))
