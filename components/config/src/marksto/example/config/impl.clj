(ns marksto.example.config.impl
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]))

(defn- read-config [cfg-str]
  (ig/read-string cfg-str))

(defn- env->lookup-map []
  (update-keys (System/getenv) symbol))

(defn load! [file profile]
  (-> (slurp (io/resource file))
      (read-config)
      (ig/deprofile [profile])
      (ig/bind (env->lookup-map))))
