# kosa [m.]: _store-room; treasury_

Library and Mobile services written in Clojure + Crux.


## Mobile App API

The Pariyatti mobile app will consume the API as specified [here](https://github.com/pariyatti/kosa/blob/master/docs/api.md).

### Option 1: Use the Sandbox server

[http://kosa-sandbox.pariyatti.org](http://kosa-sandbox.pariyatti.org) - Use this option if you will working exclusively on the mobile app without modifying or debugging the server.

### Option 2: Set up a local development server

Follow the instructions under [Development](https://github.com/pariyatti/kosa#development). Use this option if you need to modify or debug the server itself.

## Deployment

TODO: Move Ansible deployment from `kosa-rails` and adjust for Clojure + Crux.

## Development

### Install Java

Install OpenJDK 11 for:

- [Mac OS](https://gist.github.com/deobald/00b16090a932c793379cae6422206491)
- Linux: `sudo apt-get install openjdk-11-jdk`

### Install Clojure
Install `clojure` as described [here](https://www.clojure.org/guides/getting_started)

### Install Leinengen
Install `lein` as described [here](https://leiningen.org/#install)

### Install NPM
Install `npm` (nodejs LTS) as described [here](https://nodejs.org/en/download/). Sorry.

### Build kosa

```shell
git clone git@github.com:pariyatti/kosa.git
cd kosa

make help # see all the build commands
make init # setup your system

make test # run the tests
make run  # run the server
make repl # run the server interactively
```


## Design Wireframes

- [Mobile (Admin)](https://whimsical.com/4tTbGHDiYkYXj7cUnTBSTb)
- [Library](https://whimsical.com/6LN2LDkv1bRyyuojyiJ8oV)

## Architectural Thinking

- [Architecture Decision Records (ADRs)](https://github.com/pariyatti/kosa/tree/master/docs/arch)
- [Pariyatti Network Topology](https://github.com/pariyatti/agga/blob/master/docs/network-topology.pdf)
- [The Pariyatti Data Model](https://github.com/pariyatti/agga/blob/master/docs/data-models.pdf)
- [What is a (relational) database?](https://docs.google.com/document/d/1QuiWPaAUH9_UOeBouGGCgF_FyRRhoL4uLkfKvSsbw2o/edit#)
- [What is a graph database?](https://neo4j.com/developer/graph-database/)

## Old RSS Feeds

- [Dhamma Podcast Feed](http://feeds.pariyatti.org/dhammapodcasts)
- [Daily RSS Feed](https://www.pariyatti.org/Free-Resources/Daily-Words/RSS-Feeds)

## License and Copyright

AGPL-3
Copyright (c) 2019-present, Pariyatti
