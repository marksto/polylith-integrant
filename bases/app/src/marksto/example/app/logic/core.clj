(ns marksto.example.app.logic.core
  (:require [marksto.example.logic.interface :as logic]
            [marksto.example.supplier.interface :as supplier]))

(defn build-app-ctx
  [ig-system extra-ctx]
  ;; NB: This is the place where one can be creative. All required system state
  ;;     and/or its derivatives can be "injected" here into the app context map
  ;;     to be passed on to the application logic component in a myriad of ways.
  ;;     In a typical web application, the resulting map is then usually merged
  ;;     into an incoming request via a dedicated middleware.
  (merge {:system (update-keys ig-system #(keyword (name %)))
          :supply (fn [qty]
                    (let [thing (supplier/rand-thing)]
                      {:thing    thing
                       :quantity qty
                       :samples  (supplier/supply-many thing qty)}))}
         extra-ctx))

(defn run-app!
  [ig-system extra-ctx]
  (let [app-ctx (build-app-ctx ig-system extra-ctx)]
    (logic/do-something! app-ctx)))
