env = sandbox
.PHONY: help sass icons tools deps assets init test run repl
# HELP sourced from https://gist.github.com/prwhite/8168133

# Add help text after each target name starting with '\#\#'
# A category can be added with @category
HELP_FUNC = \
    %help; \
    while(<>) { \
        if(/^([a-z0-9_-]+):.*\#\#(?:@(\w+))?\s(.*)$$/) { \
            push(@{$$help{$$2}}, [$$1, $$3]); \
        } \
    }; \
    print "usage: make [target]\n\n"; \
    for ( sort keys %help ) { \
        print "$$_:\n"; \
        printf("  %-20s %s\n", $$_->[0], $$_->[1]) for @{$$help{$$_}}; \
        print "\n"; \
    }

help: ##@Miscellaneous Show this help.
	@perl -e '$(HELP_FUNC)' $(MAKEFILE_LIST)

# Hidden@Setup Sass
sass:
	$(info Installing Sass...)
	sudo npm install -g sass

# Hidden@Setup Clarity Icons
icons:
	$(info Installing Clarity Icons...)
	npm install @webcomponents/custom-elements@1.0.0 --save
	cp node_modules/@webcomponents/custom-elements/custom-elements.min.js resources/public/js/custom-elements.min.js
	npm install @clr/icons@4.0.3 --save
	cp node_modules/@clr/icons/clr-icons.min.css resources/public/css/clr-icons.min.css
	cp node_modules/@clr/icons/clr-icons.min.js resources/public/js/clr-icons.min.js
	rm -rf node_modules

~/.kosa:
	mkdir -p ~/.kosa

tmp/storage:
	mkdir -p tmp/storage

resources/storage:
	mkdir -p resources/storage

# Hidden@Setup Install Tools
tools: sass icons

deps: ##@Development Run `lein deps`
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein deps

assets: ##@Development Rebuild web assets (CSS, CLJS)
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein scss :development once
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein cljsbuild once

css: ##@Development Rebuild CSS manually
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein scss :development once

css-auto: ##@Development Rebuild CSS continuously (kinda broken?)
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein scss :development auto

cljs-auto: ##@Development Rebuild CLJS continuously
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein cljsbuild auto

init: ~/.kosa tmp/storage resources/storage tools deps assets ##@Setup Dev Setup

txt-clean: ##@Setup Remove all TXT-related directories
	rm -rf txt/pali   && mkdir -p txt/pali   && touch txt/pali/.keep
	rm -rf txt/buddha && mkdir -p txt/buddha && touch txt/buddha/.keep
	rm -rf txt/dohas  && mkdir -p txt/dohas  && touch txt/dohas/.keep
	rm -rf /tmp/daily_emails_rss_auto

txt-clone: ##@Setup Copy TXT files from private repo
	./bin/copy-txt-files.sh

routes: ##@Development Display HTTP routes
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein run -- --routes -f config/config.dev.edn

db-create: ##@Development Create a migrator: make db-create name=xyz
ifdef name
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-create $(name)
else
	echo "'name' was not defined."
endif

db-clean: ##@Development Erase local db
	rm -rf data/dev
	rm -rf data/test
	rm -rf resources/storage/*

db-migrate: ##@Development Migrate up
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-migrate dev

db-seed: ##@Development Add sample data
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-seed dev

db-reset: db-clean db-migrate db-seed ##@Development Clean, Migrate, Seed.

db-txt-pali: ##@Development Add Looped Pali Words
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-txt-pali

db-txt-buddha: ##@Development Add Looped Words of Buddha
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-txt-buddha

db-txt-doha: ##@Development Add Looped Dohas
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-txt-doha

db-txt-all: db-txt-pali db-txt-buddha db-txt-doha ##@Development Add all (3x) looped cards

test: ##@Development Run tests with lein
#lein run -- -mf config/config.test.edn
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein test

test-unit: ##@Development Run unit tests with lein
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein test :unit

test-integration: ##@Development Run integration tests with lein
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein test :integration

t: ##@Development Run 1 test: `make t TEST=your.test.ns-test`
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein test :only ${TEST}

run: ##@Development Start a development server
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein run -- -sf config/config.dev.edn

repl: ##@Development Start a Clojure REPL
	$(info Run `(start!)` in the REPL to start the server.)
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein repl

db-migrate-prod: ##@Production Migrate up
	rm -rf ./data/prod/index-store
	rm -rf ./data/prod/lucene-dir
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-migrate prod

db-txt-prod: ##@Production Add all (3x) looped cards
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-txt-pali   prod
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-txt-buddha prod
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-txt-doha   prod

run-prod: ##@Production Start a production server
	XTDB_ENABLE_BYTEUTILS_SHA1=true lein with-profile prod run -- -sf config/config.prod.edn
