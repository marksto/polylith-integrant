(ns marksto.example.pg-ops.interface
  "A set of PostgreSQL-specific operations as a Polylith component"
  (:require [marksto.example.pg-ops.impl :as impl]))

(defn ->db-spec
  [db+creds]
  (impl/->db-spec db+creds))

(defn query-version
  [data-source]
  (impl/query-version data-source))
