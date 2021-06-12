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

tmp/storage:
	mkdir -p tmp/storage

resources/storage:
	mkdir -p resources/storage

# Hidden@Setup Install Tools
tools: sass icons

deps: ##@Development Run `lein deps`
	lein deps

assets: ##@Development Rebuild web assets (CSS, CLJS)
	lein scss :development once
	lein cljsbuild once

css: ##@Development Rebuild CSS manually
	lein scss :development once

css-auto: ##@Development Rebuild CSS continuously (kinda broken?)
	lein scss :development auto

cljs-auto: ##@Development Rebuild CLJS continuously
	lein cljsbuild auto

init: tmp/storage resources/storage tools deps assets ##@Setup Dev Setup

txt-clean: ##@Setup Remove all TXT-related directories
	rm -rf txt/pali   && mkdir -p txt/pali   && touch txt/pali/.keep
	rm -rf txt/buddha && mkdir -p txt/buddha && touch txt/buddha/.keep
	rm -rf txt/dohas  && mkdir -p txt/dohas  && touch txt/dohas/.keep
	rm -rf /tmp/daily_emails_rss_auto

txt-clone: ##@Setup Copy TXT files from private repo
	./bin/copy-txt-files.sh

routes: ##@Development Display HTTP routes
	lein run -- --routes -f config/config.dev.edn

db-create: ##@Development Create a migrator: make db-create name=xyz
ifdef name
	lein db-create $(name)
else
	echo "'name' was not defined."
endif

db-clean: ##@Development Erase local db
	rm -rf data/dev
	rm -rf data/test
	rm -rf resources/storage/*

db-migrate: ##@Development Migrate up
	lein db-migrate dev

db-seed: ##@Development Add sample data
	lein db-seed dev

db-reset: db-clean db-migrate db-seed ##@Development Clean, Migrate, Seed.

db-txt-pali: ##@Development Add Looped Pali Words
	lein db-txt-pali

db-txt-buddha: ##@Development Add Looped Words of Buddha
	lein db-txt-buddha

db-txt-doha: ##@Development Add Looped Dohas
	lein db-txt-doha

test: ##@Development Run tests with lein
#lein run -- -mf config/config.test.edn
	lein test

test-unit: ##@Development Run unit tests with lein
	lein test :unit

test-integration: ##@Development Run integration tests with lein
	lein test :integration

t: ##@Development Run 1 test: `make t TEST=your.test.ns-test`
	lein test :only ${TEST}

run: ##@Development Start a development server
	lein run -- -sf config/config.dev.edn

repl: ##@Development Start a Clojure REPL
	$(info Run `(start!)` in the REPL to start the server.)
	lein repl

run-prod: ##@Production Start a production server
	lein run -- -sf config/config.prod.edn
