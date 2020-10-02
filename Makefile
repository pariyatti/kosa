env = sandbox
.PHONY: help sass tools deps assets init test run repl
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
    print "usage: make [target] env=<sandbox>\n\n"; \
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

# Hidden@Setup Install Tools
tools: sass

deps: ##@Development Pull JVM deps
	lein deps

assets: ##@Development Rebuild web assets (CSS)
	lein scss :development once

init: tools deps assets ##@Setup Dev Setup

test: ##@Development Run tests with lein
#lein run -- -mf config/config.test.edn # TODO: add migrations
	lein test

run: ##@Development Start a development server
	lein run -- -sf config/config.dev.edn

repl: ##@Development Start a Clojure REPL
	$(info Run `(start!)` in the REPL to start the server.)
	lein repl
