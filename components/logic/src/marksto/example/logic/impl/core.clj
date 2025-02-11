(ns marksto.example.logic.impl.core
  (:require [java-time.api :as jt]
            [marksto.example.logic.impl.db-ops :as db-ops]
            [marksto.example.timepiece.interface :as timepiece]))

;; NB: To keep this example app minimalistic we just query a PostgreSQL version
;;     and return the current timestamp at the time of querying.

(def default-clock (jt/system-clock "UTC"))

(defn do-something!
  [{:keys [db clock] :as _ctx}]
  {:pre [(some? db)]}
  (let [current-ts (timepiece/now (or clock default-clock))]
    {:current-ts current-ts
     :pg-version (db-ops/query-pg-version db)}))
