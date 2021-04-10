(ns kuti.fixtures.file-fixtures
  (:require [clojure.java.io :as io]))

(defn with-fixture-files [t]
  (io/copy "test/kuti/fixtures/files/bodhi.jpg" "test/kuti/fixtures/files/bodhi-temp.jpg")
  (t))
