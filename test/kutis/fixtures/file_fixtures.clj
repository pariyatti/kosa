(ns kutis.fixtures.file-fixtures
  (:require [clojure.java.io :as io]))

(defn copy-fixture-files [t]
  (io/copy "test/kutis/fixtures/files/bodhi.jpg" "test/kutis/fixtures/files/bodhi-temp.jpg")
  (t))
