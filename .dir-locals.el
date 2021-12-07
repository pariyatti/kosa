;; (setenv "XTDB_ENABLE_BYTEUTILS_SHA1" "true")

((clojure-mode
  (eval progn
        (make-local-variable 'process-environment)
        (setenv "XTDB_ENABLE_BYTEUTILS_SHA1" "true"))))
