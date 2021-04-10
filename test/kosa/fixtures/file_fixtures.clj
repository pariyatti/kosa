(ns kosa.fixtures.file-fixtures
  (:require [clojure.java.io :as io]
            [babashka.fs :as fs]))

(defn file [filename]
  (io/file (fs/path "test/kosa/fixtures/files" filename)))
