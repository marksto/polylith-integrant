(ns marksto.example.logic.impl.core
  (:require [java-time.api :as jt]
            [marksto.example.logic.impl.db-ops :as db-ops]
            [marksto.example.timepiece.interface :as timepiece]))

(def default-clock (jt/system-clock "UTC"))

(defn do-something!
  [{:keys [system supply clock] :as _ctx}]
  {:pre [(some? (:db system)) (fn? supply)]}
  (let [current-ts (timepiece/now (or clock default-clock))
        pg-version (db-ops/query-pg-version (:db system))
        supplement (supply (inc (rand-int 3)))]
    {:current-ts current-ts
     :pg-version pg-version
     :supplement supplement}))
