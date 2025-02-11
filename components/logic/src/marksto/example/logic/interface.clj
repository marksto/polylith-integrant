(ns marksto.example.logic.interface
  "The application logic as a Polylith component"
  (:require [marksto.example.logic.impl.core :as impl]))

(defn do-something!
  "Given an app context `ctx`, does something useful.

   Returns a map with the `:pg-version` key mapped to the PostgreSQL version."
  {:arglists '([{:keys [db] :as ctx}])}
  [ctx]
  (impl/do-something! ctx))
