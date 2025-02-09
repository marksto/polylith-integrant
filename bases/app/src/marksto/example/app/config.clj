(ns marksto.example.app.config
  (:require [marksto.example.config.interface :as config]))

(defn- get-profile []
  (or (some-> (System/getenv "PROFILE") (keyword))
      (throw (Exception. "Please, set the 'PROFILE' env var"))))

(defn load!
  ([]
   (load! (get-profile)))
  ([profile]
   (config/load! "app/config.edn" profile)))
