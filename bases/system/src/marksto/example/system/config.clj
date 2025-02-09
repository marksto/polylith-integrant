(ns marksto.example.system.config
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [marksto.example.config.interface :as config]))

(defmethod ig/init-key :marksto.example.system/config
  [_ _]
  (log/info "Loading configuration")
  (config/load-config))
