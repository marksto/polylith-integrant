(ns marksto.example.config.impl
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]))

;; NB: In a more realistic use case, the configuration is loaded in 2 stages:
;;     1. Read the entire content of the file using something smarter (Aero);
;;     2. Remove all non-component keys from the map to keep Integrant happy.

(defn- read-config [cfg-str]
  (ig/read-string cfg-str))

(defn- env->lookup-map []
  (update-keys (System/getenv) symbol))

(defn load! [file profile]
  (-> (slurp (io/resource file))
      (read-config)
      (ig/deprofile [profile])
      (ig/bind (env->lookup-map))))
