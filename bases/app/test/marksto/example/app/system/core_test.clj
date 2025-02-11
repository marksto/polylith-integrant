(ns marksto.example.app.system.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [marksto.example.app.system.core :as sut]
            [marksto.example.config.interface :as config]
            [marksto.example.logic.interface :as logic]))

(def test-ig-config (config/load! "app/test-config.edn" :dev))

(defn- get-pg-version
  [{{:marksto.example.app.system/keys [db]} :system :as _system-state}]
  (logic/do-something! {:db db}))

(deftest integrant-system-lifecycle
  (comment
    "Scenario tests the production use of the Integrant system")

  (testing "System init succeeds"
    (sut/init! test-ig-config)
    (let [system-state (sut/get-state)]
      (is (map? system-state))
      (is (contains? system-state :system))
      (is (contains? system-state :config))
      (is (= test-ig-config (:config system-state)))))

  (testing "All system components are initialized and functional"
    (let [{:keys [pg-version]} (get-pg-version (sut/get-state))]
      (is (str/starts-with? pg-version "PostgreSQL 17.0"))))

  (testing "System halt succeeds"
    (sut/halt! (:system (sut/get-state)))
    (let [system-state (sut/get-state)]
      (is (nil? system-state))
      (is (thrown? Throwable (get-pg-version system-state))))))

(comment
  (run-tests)
  .)
