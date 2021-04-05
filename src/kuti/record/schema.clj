(ns kuti.record.schema
  (:require [kuti.record.core :as core]
            [kuti.support.digest :refer [uuid]]
            [kuti.support.time :as time]
            [kuti.support.debugging :refer :all])
  (:import [java.math BigDecimal BigInteger]
           [java.lang Boolean Double Float Long String]
           [java.util Date UUID]
           [java.time Instant]
           [clojure.lang Keyword Symbol PersistentVector]
           [java.net URI]))

(def meta-keys #{:crux.db/id :type :updated-at :published-at})

(defn non-homogenous? [e]
  (if-let [type (:type e)]
    (remove #(= type (-> % namespace keyword))
            (keys (apply dissoc e meta-keys)))
    (throw (IllegalArgumentException. ":type key not found."))))

(defn missing-type? [t db-types]
  (assert (> (count db-types) 0)
          (format "Saved failed. DB is missing type for entity of type '%s'." t))
  db-types)

(defn missing-key? [m k]
  (when (not (contains? m k))
    k))

(defn missing-keys? [m ks]
  (->> ks
       (map #(missing-key? m %))
       (remove nil?)))

(defn assert-required-attrs [e]
  (let [type (:type e)
        missing-type-check (partial missing-type? type)
        attrs (-> (core/query-raw '{:find [e attrs]
                                    :where [[e :db.entity/type t]
                                            [e :db.entity/attrs attrs]]
                                    :in [t]} type)
                  missing-type-check
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
  (->> (core/query-raw '{:find [e vt]
                         :where [[e :db/ident ident]
                                 [e :db/valueType vt]]
                         :in [ident]} k)
       first))

(def value-types
  {:db.type/bigdec  java.math.BigDecimal
   :db.type/bigint  java.math.BigInteger
   :db.type/boolean java.lang.Boolean
   :db.type/double  java.lang.Double
   :db.type/float   java.lang.Float
   :db.type/instant java.util.Date
   :db.type/inst    java.time.Instant
   :db.type/keyword clojure.lang.Keyword
   :db.type/long    java.lang.Long
   ;; :db.type/ref  nil ;; refs in Crux are implicit so this is not implemented
   :db.type/string  java.lang.String
   :db.type/symbol  clojure.lang.Symbol
   :db.type/tuple   clojure.lang.PersistentVector
   :db.type/uuid    java.util.UUID
   :db.type/uri     java.net.URI
   :db.type/bytes   (Class/forName "[B")})

(defn class-for [vt s]
  (assert (contains? value-types vt)
          (format "No :db/valueType of '%s' exists for schema '%s'."
                  vt s))
  (clojure.core/get value-types vt))

(defn assert-schema [e s]
  (let [k (:db/ident s)
        f (clojure.core/get e k)
        c (class f)
        v (:db/valueType s)
        t (class-for v s)]
    (assert (= c t)
            (format "Class %s of field %s does not match value type %s" c k t))
    k))

(defn coerce-schema-1 [e s]
  (let [k (:db/ident s)
        f (clojure.core/get e k)
        f2 (case (:db/valueType s)
             :db.type/bigint  (biginteger f)
             :db.type/boolean (boolean f)
             :db.type/double  (double f)
             :db.type/float   (float f)
             :db.type/inst    (time/instant f)
             :db.type/long    (long f)
             f)]
    (assoc e k f2)))

(defn coerce-schema [e s]
  (reduce coerce-schema-1 e s))

;; public API:

(defn add-type
  ([t attrs]
   (add-type core/crux-node t attrs))
  ([node t attrs]
   (core/transact! node
                   [[:crux.tx/put
                     {:crux.db/id      t
                      :db.entity/type  t
                      :db.entity/attrs attrs}]])))

(defn remove-type
  ([t]
   (remove-type core/crux-node t))
  ([node t]
   (core/transact! node
                   [[:crux.tx/delete t]])))

(defn add-schema
  ([attr value-type]
   (add-schema core/crux-node attr value-type))
  ([node attr value-type]
   (core/transact! node [[:crux.tx/put
                          {:crux.db/id   attr
                           :db/ident     attr
                           :db/valueType value-type}]])))

(defn remove-schema
  ([s]
   (remove-schema core/crux-node s))
  ([node s]
   (core/transact! node
                   [[:crux.tx/delete s]])))

(defn save! [e]
  (assert (contains? e :type) ":type key expected.")
  (assert (empty? (non-homogenous? e))
          (format "Some keys did not match specified :type. %s"
                  (clojure.string/join ", " (non-homogenous? e))))
  (assert-required-attrs e)
  (let [e2 (into {} (map coerce e))
        schema (->> (disj (-> e2 keys set)
                          :type :crux.db/id :updated-at)
                    (map schema-for)
                    (remove nil?))
        e3 (coerce-schema e2 schema)]
    (->> schema
         (map #(assert-schema e3 %))
         (core/put e3))))
