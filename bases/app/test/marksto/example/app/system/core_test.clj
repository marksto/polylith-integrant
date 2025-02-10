(ns marksto.example.app.system.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [marksto.example.config.interface :as config]
            [marksto.example.pg-ops.interface :as pg-ops]
            [marksto.example.app.system.core :as sut]))

(def test-ig-config (config/load! "app/test-config.edn" :dev))

(defn- get-pg-version [system-state]
  (pg-ops/query-version
    (get-in system-state [:system :marksto.example.app.system/data-source])))

(deftest integrant-system-lifecycle
  (comment
    "Scenario tests the production use of the Integrant system")

  (testing "System init succeeds"
    (is (= ::sut/initiated (sut/init! test-ig-config)))
    (let [system-state (sut/get-state)]
      (is (map? system-state))
      (is (contains? system-state :system))
      (is (contains? system-state :config))
      (is (= test-ig-config (:config system-state)))))

  (testing "All system components are initialized and functional"
    (let [system-state (sut/get-state)
          pg-version (get-pg-version system-state)]
      (is (str/starts-with? pg-version "PostgreSQL 17.0"))))

  (testing "System halt succeeds"
    (is (= ::sut/halted (sut/halt!)))
    (let [system-state (sut/get-state)]
      (is (nil? system-state))
      (is (thrown? Exception (get-pg-version system-state))))))

(comment
  (run-tests)
  .)
