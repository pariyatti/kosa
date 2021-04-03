(ns kuti.record.schema
  (:require [kuti.record.core :as core]
            [kuti.support.time :as time]
            [kuti.support.debugging :refer :all])
  (:import [java.math BigDecimal BigInteger]
           [java.lang String Boolean]))

(defn non-homogenous? [e]
  (if-let [type (:type e)]
    (remove #(= type (-> % namespace keyword))
            (keys (dissoc e :crux.db/id :type)))
    (throw (IllegalArgumentException. ":type key not found."))))

(defn missing-key? [m k]
  (when (not (contains? m k))
    k))

(defn missing-keys? [m ks]
  (->> ks
       (map #(missing-key? m %))
       (remove nil?)))

(defn assert-required-attrs [e]
  (let [type (:type e)
        attrs (-> (core/query '{:find [e attrs]
                                  :where [[e :db.entity/type t]
                                          [e :db.entity/attrs attrs]]
                                  :in [t]} type)
                  first
                  :db.entity/attrs)
        missing (missing-keys? e attrs)]
    (assert (empty? missing)
            (format "Saved failed. Missing key(s) for entity of type '%s': %s"
                    type (clojure.string/join ", " missing)))))

(defn coerce [[k v]]
  (let [value (condp isa? v
                clojure.lang.BigInt (biginteger v)
                java.lang.Double (double v)
                v)]
    [k value]))

(defn schema-for [k]
  (-> (core/query '{:find [e vt]
                    :where [[e :db/ident ident]
                            [e :db/valueType vt]]
                    :in [ident]} k)
      first))

(def value-types
  {:db.type/bigdec  java.math.BigDecimal
   :db.type/bigint  java.math.BigInteger
   :db.type/string  java.lang.String
   :db.type/boolean java.lang.Boolean
   :db.type/double  java.lang.Double
   :db.type/float   java.lang.Float
   :db.type/instant java.util.Date
   :db.type/inst    java.time.Instant
   ;; TODO: keyword
   ;; TODO: long
   ;; TODO: string
   ;; TODO: symbol
   ;; TODO: tuple
   ;; TODO: uuid
   ;; TODO: URI
   ;; TODO: bytes
   })

(defn class-for [vt]
  (assert (contains? value-types vt)
          (format "No :db/valueType of '%s' exists." vt))
  (clojure.core/get value-types vt))

(defn assert-schema [e s]
  (let [k (:db/ident s)
        f (clojure.core/get e k)
        c (class f)
        v (:db/valueType s)
        t (class-for v)]
    (assert (= c t)
            (format "Class %s of field %s does not match value type %s" c k t))
    k))

(defn coerce-schema-1 [e s]
  (let [k (:db/ident s)
        f (clojure.core/get e k)
        f2 (case (:db/valueType s)
             :db.type/float (float f)
             :db.type/inst  (time/instant f)
             f)]
    (assoc e k f2)))

(defn coerce-schema [e s]
  (reduce coerce-schema-1 e s))

;; public API:

(defn add-type [t attrs]
  (core/put {:db.entity/type  t
             :db.entity/attrs attrs}
            [:db.entity/type :db.entity/attrs]))

(defn add-schema [attr value-type]
  (core/put {:db/ident     attr
             :db/valueType value-type}
            [:db/ident :db/valueType]))

(defn save! [e]
  (assert (contains? e :type) ":type key expected.")
  (assert (empty? (non-homogenous? e))
          (format "Some keys did not match specified :type. %s"
                  (clojure.string/join ", " (non-homogenous? e))))
  (assert-required-attrs e)
  (let [e2 (into {} (map coerce e))
        schema (->> (disj (-> e2 keys set)
                          :type :crux.db/id :updated-at)
                    (map schema-for))
        e3 (coerce-schema e2 schema)]
    (->> schema
         (map #(assert-schema e3 %))
         (core/put e3))))
