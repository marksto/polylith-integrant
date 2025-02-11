(ns marksto.example.logic.impl.db-ops
  (:require [next.jdbc :as jdbc]))

(defn query-pg-version
  [conn]
  (some-> (jdbc/execute-one! conn ["SELECT version()"])
          :version))
