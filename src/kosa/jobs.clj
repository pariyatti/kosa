(ns kosa.jobs
  [:require
   ;; NOTE: jobs must be required before `kuti.job` is mounted
   ;;       so that `resolve` never returns nil:
   [kosa.mobile.today.pali-word.rss-job]
   [kosa.mobile.today.looped-pali-word.publish-job]
   [kosa.mobile.today.looped-words-of-buddha.publish-job]
   [kosa.mobile.today.looped-doha.publish-job]])
