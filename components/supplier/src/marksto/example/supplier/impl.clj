(ns marksto.example.supplier.impl
  (:require [talltale.core]))

(def supported-things-vec
  [;; Common
   :text
   :lorem-ipsum

   ;; Address
   :street-number
   :street
   :postal-code
   :city
   :phone-number
   :address

   ;; Company
   #_:company-id
   :company-name
   :company-type
   :function
   :email
   #_:full-name
   #_:company-email
   :identification-number
   :tld
   #_:domain
   #_:url
   #_:logo-url
   :company

   ;; Person
   :sex
   :username
   :random-password
   :first-name
   :first-name-male
   :first-name-female
   :last-name
   :last-name-male
   :last-name-female
   :age
   :date-of-birth
   :position
   :picture-url
   :person-male
   :person-female
   :person

   ;; Misc
   :quality
   :shape
   :color
   :animal
   :landform])

(def supported-things-set (set supported-things-vec))

(defn rand-thing []
  (nth supported-things-vec (rand-int (count supported-things-vec))))

(defn -validate
  [thing]
  (when-not (contains? supported-things-set thing)
    (throw (IllegalArgumentException. "Unsupported thing"))))

(defn -supply-one
  [thing]
  ((resolve (symbol "talltale.core" (name thing)))))

(defn supply-one
  [thing]
  (-validate thing)
  (-supply-one thing))

(defn supply-many
  [thing qty]
  (-validate thing)
  (vec (repeatedly (or qty (inc (rand-int 10))) #(-supply-one thing))))
