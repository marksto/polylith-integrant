(ns marksto.example.pg-ops.interface
  "A set of PostgreSQL-specific operations as a Polylith component"
  (:require [marksto.example.pg-ops.core :as core]))

(defn ->db-spec
  [db+creds]
  (core/->db-spec db+creds))

(defn query-version
  [data-source]
  (core/query-version data-source))
