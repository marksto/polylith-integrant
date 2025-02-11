(ns marksto.example.app.system.test-utils
  (:require [integrant.core :as ig])
  (:import (clojure.lang MultiFn)))

(defn- now []
  (System/nanoTime))

(defn- already-spied? [method-fn]
  (boolean (::calls (meta method-fn))))

(defn- spy-method-fn
  [method-fn]
  (if (already-spied? method-fn)
    method-fn
    (let [*calls (atom [])]
      (with-meta (fn [& args]
                   (try
                     (let [ret (apply method-fn args)]
                       (swap! *calls conj {:ts     (now)
                                           :args   args
                                           :return ret})
                       ret)
                     (catch Exception e
                       (swap! *calls conj {:ts    (now)
                                           :args  args
                                           :throw e})
                       (throw e))))
                 {::calls *calls}))))

(defn spy-all-methods!
  [^MultiFn multifn]
  (doseq [[dispatch-val method-fn] (methods multifn)]
    (remove-method multifn dispatch-val)
    (MultiFn/.addMethod multifn dispatch-val (spy-method-fn method-fn))))

;;

(defn method-calls
  [dispatch-val method-fn]
  (if-some [*calls (::calls (meta method-fn))]
    (first (swap-vals! *calls (constantly [])))
    (throw (IllegalArgumentException.
             (format "The method for '%s' was not spied on" dispatch-val)))))

(defn all-method-calls
  [^MultiFn multifn]
  (->> (methods multifn)
       (mapcat (fn [[dispatch-val method-fn]]
                 (method-calls dispatch-val method-fn)))
       (sort-by :ts)
       (into [])))

;;

(defn load-namespaces-and-spy-methods
  ([config]
   (ig/load-namespaces config)
   (spy-all-methods! ig/init-key)
   (spy-all-methods! ig/halt-key!))
  ([config keys]
   (ig/load-namespaces config keys)
   (spy-all-methods! ig/init-key)
   (spy-all-methods! ig/halt-key!)))
