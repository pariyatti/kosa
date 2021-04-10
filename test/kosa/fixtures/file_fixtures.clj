(ns kosa.fixtures.file-fixtures
  (:require [babashka.fs :as fs]))

(defn file [filename]
  (fs/file (fs/path "test/kosa/fixtures/files" filename)))
