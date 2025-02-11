(ns marksto.example.config.interface-test
  (:require [clojure.test :refer [deftest is testing]]
            [marksto.example.config.interface :as sut]))

(deftest load!-test
  (testing "The dummy config file is successfully read"
    (let [{:keys [alpha bravo charlie]} (sut/load! "dummy-config.edn" :one)]
      (is (= 2025 alpha)
          "Plain options are read")
      (is (= (System/getenv "JAVA_HOME") bravo)
          "Environment variables are read")
      (is (= "Option for the profile `:one`" charlie)
          "Profile-specific options are read"))))

(comment
  (clojure.test/run-tests)
  .)
