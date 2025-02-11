(ns marksto.example.app.system.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [integrant.core :as ig]
            [marksto.example.app.config :as config]
            [marksto.example.app.system.core :as sut]
            [marksto.example.app.system.test-utils :as tu]))

(defn with-integrant-methods-spy [f]
  (with-redefs [sut/load-namespaces tu/load-namespaces-and-spy-methods]
    (f)))

(use-fixtures :once with-integrant-methods-spy)

;;

;; NB: A dedicated `:test` profile can be used if you see fit.
(def test-ig-config (config/load! "app/test-config.edn" :dev))

(deftest integrant-system-lifecycle-test
  (comment
    "Scenario tests the production use of the Integrant system")

  (testing "System init succeeds"
    (sut/init! test-ig-config)

    (let [system-state (sut/get-state)
          ig-init-key-calls (tu/all-method-calls ig/init-key)]
      (is (map? system-state)
          "The system is initiated without any exceptions")
      (is (= test-ig-config (:config system-state))
          "The system is initiated with the test config")
      (is (= #{:marksto.example.app.system/embedded-pg
               :marksto.example.app.system/db}
             (set (keys (:system system-state))))
          "All system components should be initiated")
      (is (= [:marksto.example.app.system/embedded-pg
              :marksto.example.app.system/db]
             (map (comp first :args) ig-init-key-calls))
          "The system components should be initiated in the correct order")))

  (testing "System halt succeeds"
    (sut/halt! (:system (sut/get-state)))

    (let [system-state (sut/get-state)
          ig-halt-key!-calls (tu/all-method-calls ig/halt-key!)]
      (is (nil? system-state)
          "The system is halted without any exceptions")
      (is (= [:marksto.example.app.system/db
              :marksto.example.app.system/embedded-pg]
             (map (comp first :args) ig-halt-key!-calls))
          "All system components should be halted in reverse order"))))

(comment
  (clojure.test/run-tests)
  .)
