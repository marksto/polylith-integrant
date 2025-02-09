(ns marksto.example.app.config
  (:require [integrant.core :as ig]))

(defn load! []
  {:marksto.example.app.system/config      {}
   :marksto.example.app.system/embedded-pg {:config (ig/ref :marksto.example.app.system/config)
                                            #_#_:log-file "path/to/pg-logs-redirection"}
   :marksto.example.app.system/data-source {:config   (ig/ref :marksto.example.app.system/config)
                                            :postgres (ig/ref :marksto.example.app.system/embedded-pg)}})
