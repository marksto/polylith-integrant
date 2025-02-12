(ns marksto.example.supplier.interface-test
  (:require [clojure.test :refer [deftest is]]
            [marksto.example.supplier.interface :as sut]))

(deftest supply-one-test
  (doseq [thing sut/supported-things]
    (is (some? (sut/supply-one thing))
        (format "Successfully supplies one '%s'" thing))))

(deftest supply-many-test
  (let [thing (sut/rand-thing)
        quantity 5
        samples (sut/supply-many thing quantity)]
    (is (= quantity (count samples))
        "Exact number of things is supplied")
    (is (every? some? samples)
        (format "Successfully supplies many '%s'" thing))))

(comment
  (clojure.test/run-tests)
  .)
