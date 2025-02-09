(ns marksto.example.pg-ops.interface
  "A set of PostgreSQL-specific operations as a Polylith component"
  (:require [marksto.example.pg-ops.impl :as impl]))

(defn query-version
  "Given a DB connection `conn`, queries the PostgreSQL version."
  [conn]
  (impl/query-version conn))
