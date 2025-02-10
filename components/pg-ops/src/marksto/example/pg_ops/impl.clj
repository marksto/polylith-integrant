(ns marksto.example.pg-ops.impl
  (:require [next.jdbc :as jdbc]))

(defn query-version
  [conn]
  (some-> (jdbc/execute-one! conn ["SELECT version()"])
          :version))
