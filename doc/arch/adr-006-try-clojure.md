# Architecture Decision Record: Try Clojure

## Context

The reasoning behind the move to Clojure is three-fold:

(1) It was non-trivial to get Rails to talk to Neo4j, and even that was only for basic data modeling cases. The Ruby libraries I spent weeks bringing up to speed may drop Neo4j support in the future or may require even more updates from us, since only a handful of people seem interested in the Ruby/Rails+Neo4j combination. Those libraries are many thousands of lines of code, in total, and I worry that without an energetic Ruby/Neo4j community, we might paint ourselves into a corner by depending on them.

(2) Ruby does not seem to have the surface area I thought it had. But in addition to this, volunteers who come forward seem either willing to learn whatever language we happen to have chosen or vastly prefer a powerful JVM language like Clojure.

(3) Kotlin seems like a sensible language in the JVM middle-ground but it's hard to say it has much more weight than Clojure now that Clojure is over a decade old. Most of the volunteers who've come forward and actively contributed to kosa actually have a strong preference for Clojure. Rather than insist on another "boring and safe" language like Java or Kotlin after two failed attempts with Python and Ruby, I'm quite happy with how things are going so far in a language the development team actually enjoys using.

## Decision

We've been experimenting with moving Kosa from Ruby to a JVM language and it's been going well. Our "experiment" is more or less simply to re-implement what we have in Ruby in Clojure (and to stop and reconsider the move if we run into issues along the way). That experiment is in the `kosa-crux` repository on GitHub. All of @varunpai 's design work and the vast majority of our deployment infrastructure (Ansible scripts) can move from the Ruby codebase wholesale, without any rework. I won't call the experiment a success until we've implemented the major components which exist in the Ruby codebase, but we feel quite close and nothing has slowed development down so far. When we reach that point, we'll start implementing the v1 features the Ruby code lacks: RSS feed parsing, daily doha / dwob cards, etc.

## Status

Pending.

## Consequences

1. Clojure is a more friendly language for developers and has a wider community of willing volunteers.
2. Clojure is a better language to work with both Neo4j (if we choose that database) and Crux (which we are currently evaluating in parallel).
3. Clojure libraries required in the Pariyatti library are much less burdensome than the equivalent Ruby libraries.
