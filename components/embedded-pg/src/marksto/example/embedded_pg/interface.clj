(ns marksto.example.embedded-pg.interface
  "An embedded PostgreSQL DBMS as a Polylith component"
  (:require [marksto.example.embedded-pg.impl :as impl])
  (:import
    (io.zonky.test.db.postgres.embedded EmbeddedPostgres)))

(defn start-postgres!
  [pg-config]
  (impl/start-postgres! pg-config))

(defn stop-postgres!
  [^EmbeddedPostgres embedded-pg]
  (impl/stop-postgres! embedded-pg))
