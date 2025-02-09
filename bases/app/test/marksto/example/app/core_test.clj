(ns marksto.example.app.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [marksto.example.pg-ops.interface :as pg-ops]
            [marksto.example.app.core :as system]
            [marksto.example.app.state :as state]))

(defn- get-pg-version
  []
  (pg-ops/query-version (state/get :marksto.example.app/data-source)))

(deftest integrant-system-lifecycle
  (comment
    "Scenario tests the production use of the Integrant system")

  (testing "System launch succeeds"
    (is (= ::system/started (system/launch!)))
    (let [system-state @state/*state]
      (is (map? system-state))
      (is (contains? system-state :system))
      (is (contains? system-state :config))))

  (testing "All system components are initialized and functional"
    (is (str/starts-with? (get-pg-version) "PostgreSQL")))

  (testing "System restart succeeds and config does not change"
    (let [old-config (get @state/*state :config)
          new-state  (state/-restart!)]
      (is (map? new-state))
      (is (contains? new-state :system))
      (is (contains? new-state :config))
      (is (= old-config (:config new-state)))))

  (testing "System shutdown succeeds"
    (is (= ::system/stopped (system/shutdown!)))
    (let [system-state @state/*state]
      (is (nil? system-state))
      (is (thrown? Exception (get-pg-version))))))

(comment
  (run-tests)
  .)
