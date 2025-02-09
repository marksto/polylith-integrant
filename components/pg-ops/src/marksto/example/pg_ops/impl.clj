(ns marksto.example.pg-ops.impl
  (:require [clojure.java.jdbc :as jdbc]))

(defn query-version
  [conn]
  (some-> (jdbc/query conn ["SELECT version()"])
          (first)
          :version))
