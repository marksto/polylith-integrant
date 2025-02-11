(ns marksto.example.timepiece.interface-test
  (:require [clojure.test :refer [deftest is]]
            [java-time.api :as jt]
            [marksto.example.timepiece.interface :as sut]))

(deftest now-test
  (jt/with-clock (jt/mock-clock)
    (is (= (jt/local-date-time) (sut/now (jt/mock-clock))))))

(comment
  (clojure.test/run-tests)
  .)
