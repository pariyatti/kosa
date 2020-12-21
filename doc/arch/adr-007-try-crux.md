# Architecture Decision Record: Try Crux

## Context

Neo4j suffers from a number of problems. It has rather restrictive licensing due to an older "open core" open source release model. It does not retain a first-class notion of time, nor does it maintain a full history in a transaction log as modern databases do. The Neo4j Clojure libraries probably require that we maintain them ourselves... though they are relatively light at a few hundred lines of code each.

## Decision

We're  giving serious consideration to replacing Neo4j with Crux before we launch. We're less confident about this decision than the Clojure decision, as Crux is less mature than Clojure (years vs. decades). However, Crux is built on existing, mature technologies (RocksDB, JDBC/Kafka, Datalog). It's licensing (MIT) is much more liberal than Neo4j's and after speaking at length with the project owners (Jon and Jeremy from https://juxt.pro) it seems that it's both a reasonably safe bet it will still be here in ten years but also that we can easily migrate away from Crux if need be, since it retains all the data ever written to it in an immutable transaction log. The transaction log means Crux treats the concept of "time" as a first-class citizen in the database; Neo4j does not (most older database architectures don't). However, if we find Crux lacking, we can always switch back to Neo4j quite easily, since Datalog and Cypher are comparable languages for our use cases. Crux also offers some basic architectural benefits that Neo4j does not: It has layers which provide us methods of adding stricter database schema to the data types which require it (ex. "Pali Word of the Day" card in the Today tab requires very little flexibility) and looser schema for data types which don't (ex. a newsletter may include arbitrary metadata for the search engine). Crux also has built-in full-text search which would require ElasticSearch or some other service running on top of Neo4j if we continued on that path.

## Status

Pending.

## Consequences

1. First-class notions of time _and_ built-in bi-temporality
2. First-class graph queries in Datalog (an open standard)
3. First-class Clojure db entities
4. Modular libraries built for Clojure
5. Lightweight deployments
6. Direct line of support from JUXT
