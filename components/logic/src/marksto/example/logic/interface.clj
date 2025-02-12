(ns marksto.example.logic.interface
  "The application logic as a Polylith component"
  (:require [marksto.example.logic.impl.core :as impl]))

(defn do-something!
  "Given an app context `ctx`, does something useful.

   Returns a map with the following keys:
   - `:current-ts` — the current timestamp;
   - `:pg-version` — the PostgreSQL DBMS version;
   - `:supplement` — something supplied by `supply`."
  {:arglists '([{:keys [system supply ?clock] :as ctx}])}
  [ctx]
  (impl/do-something! ctx))
