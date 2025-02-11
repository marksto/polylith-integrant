(ns marksto.example.logic.impl.core
  (:require [marksto.example.logic.impl.db-ops :as db-ops]))

;; NB: To keep this example app minimalistic we just query a PostgreSQL version.

(defn do-something!
  [{:keys [db] :as _ctx}]
  {:pre [(some? db)]}
  {:pg-version (db-ops/query-pg-version db)})
