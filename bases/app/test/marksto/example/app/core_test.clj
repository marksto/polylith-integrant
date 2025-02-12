(ns marksto.example.app.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [java-time.api :as jt]
            [marksto.example.app.config :as config]
            [marksto.example.app.core :as sut]
            [marksto.example.supplier.interface :as supplier]))

;; NB: Here we intentionally test against individual app core functions,
;;     since otherwise the OS processes that are spawned by an embedded
;;     PostgreSQL will remain hanging due to the fact that the shutdown
;;     sequence cannot be triggered by a test runner.
;;
;;     Normally, this is circumvented by wrapping the embedded Postgres
;;     into a `:once` test fixture that starts and stops itself for you.
;;     However, here we want to consider using it as a system component,
;;     and therefore we have to take care of the system shutdown.

;; NB: A dedicated `:test` profile can be used if you see fit.
(def test-ig-config (config/load! "app/test-config.edn" :dev))

;; NB: Yes, there's a dedicated macro `jt/with-clock` for exactly this
;;     purpose, but we would like to showcase passing clock downstream
;;     as a regular parameter to a Polylith component method.
(def mock-clock (jt/mock-clock (jt/instant)))

;; NB: Stubbing a particular Polylith component method via indirection.
(def someone (supplier/supply-one :person))
(defn stubbed-supply [& [_qty]]
  {:thing    :person
   :quantity 1
   :samples  [someone]})

(deftest application-logic-test
  (comment
    "Scenario tests the production use of the example application")

  (let [test-ig-system (testing "The app is successfully launched"
                         (sut/launch! test-ig-config))]
    (try
      (testing "All system components are initialized and functional"
        (let [extra-ctx {:clock  mock-clock
                         :supply stubbed-supply}
              app-res (sut/run-app-logic! test-ig-system extra-ctx)]
          (is (map? app-res))
          (is (= (jt/local-date-time mock-clock) (:current-ts app-res))
              "A current timestamp specified by a `mock-clock` is used")
          (is (str/starts-with? (:pg-version app-res) "PostgreSQL 17.0")
              "A PostgreSQL version specified in `embedded-pg` is used")
          (is (= (stubbed-supply) (:supplement app-res))
              "A supplement specified by a `stubbed-supply` fn is used")))

      (finally
        (testing "The app is successfully shutdown"
          (sut/shutdown! test-ig-system false))))))

(comment
  (clojure.test/run-tests)
  .)
